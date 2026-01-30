package es.plaiaundi.infoCam.api_java.config;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IncidenciaSyncService {

    @Autowired private IncidenciaRepository incidenciaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private final RestTemplate restTemplate;

    public IncidenciaSyncService() {
        // Configuramos el RestTemplate para que sea "inseguro" desde el nacimiento
        this.restTemplate = createInsecureRestTemplate();
    }

    // --- COPIA ESTE MÉTODO PARA CREAR EL REST TEMPLATE INSEGURO ---
    private RestTemplate createInsecureRestTemplate() {
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            // Usamos un factory simple que no verifique nombres de host
            return new RestTemplate();
        } catch (Exception e) {
            return new RestTemplate();
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void sincronizarIncidenciasDiarias() {
        try {
            configurarSslInseguroGlobal();
        } catch (Exception e) {}

        // Recoger la fecha de hoy para hacer la llamada a OpenData
        String hoy = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String url = "https://api.euskadi.eus/traffic/v1.0/incidences/byDate/" + hoy + "?_page=";

        // CONTADORES
        int nuevos = 0;
        int actualizados = 0;
        int ignorados = 0;

        try {
            int page = 1;
            int totalPages = 1;

            do {
                Map<String, Object> response = restTemplate.getForObject(url + page, Map.class);
                if (response == null) break;

                // Recoger de la respuesta el total de las páginas
                totalPages = (int) response.get("totalPages");
                List<Map<String, Object>> remoteIncidences = (List<Map<String, Object>>) response.get("incidences");

                // iterar por cada página los objetos
                for (Map<String, Object> data : remoteIncidences) {
                    String idExterno = String.valueOf(data.get("incidenceId"));
                    Incidencia datosMapeados = mapear(data);

                    //ver si el metodo a conseguido mapear correctamente los datos formateando atributos
                    if (datosMapeados == null) {
                        System.out.println("[-] IGNORADA (Filtro tipo): ID " + idExterno);
                        ignorados++;
                        continue;
                    }
                    //ver si ya existe una incidencia con ese mismo external_id
                    Optional<Incidencia> existenteOpt = incidenciaRepository.findByExternalId(idExterno);
                    //si existenteOpt es vacío significa que esa incidencia es nueva
                    if (existenteOpt.isEmpty()) {
                        // Hacer inserción
                        datosMapeados.setExternalId(idExterno);
                        incidenciaRepository.save(datosMapeados);
                        System.out.println("[+] INSERTADA: " + datosMapeados.getNombre() + " (ID: " + idExterno + ")");
                        nuevos++;
                    } //si existe esa incidencia hay que actualizarla por si hubiese cambiado algun campo
                    else {
                        // ACTUALIZACIÓN
                        Incidencia incBd = existenteOpt.get();
                        actualizarIncidenciaExistente(incBd, datosMapeados);
                        incidenciaRepository.save(incBd);
                        System.out.println("[*] ACTUALIZADA: " + incBd.getNombre() + " (ID: " + idExterno + ")");
                        actualizados++;
                    }
                }
                page++;
            } while (page <= totalPages);

            // SALIDA FINAL RESUMIDA
            System.out.println("\n--- RESUMEN DE SINCRONIZACIÓN (" + hoy + ") ---");
            System.out.println("Nuevas: " + nuevos);
            System.out.println("Actualizadas: " + actualizados);
            System.out.println("Ignoradas: " + ignorados);
            System.out.println("-----------------------------------------------\n");

        } catch (Exception e) {
            System.err.println("!!! ERROR EN SINCRONIZACIÓN: " + e.getMessage());
        }
    }

    // Este método lo puedes copiar del DataSeeder para asegurar el tiro
    private void configurarSslInseguroGlobal() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Esto también ayuda a evitar el error de nombre de host
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    private Incidencia mapear(Map<String, Object> data) {
        try {
            String tipo = String.valueOf(data.get("incidenceType"));

            // Filtro de tipos ignorados
            if ("Puertos de montaña".equalsIgnoreCase(tipo) || "Vialidad invernal tramos".equalsIgnoreCase(tipo)) {
                return null;
            }
            //recoger de la BBDD el usuario gobierno para que sea creador de estas incidencias
            Usuario gobierno = usuarioRepository.findByUsername("gov");

            // Lógica para la CAUSA (si no existe o es null -> Desconocida)
            Object causaObj = data.get("cause");
            String causa = (causaObj == null || String.valueOf(causaObj).equals("null") || String.valueOf(causaObj).isEmpty())
                    ? "Desconocida"
                    : String.valueOf(causaObj);

            // Lógica para el NOMBRE (Si no hay tipo, intentar pillar incidenceName)
            String ciudad = String.valueOf(data.get("cityTown"));
            String carretera = String.valueOf(data.get("road"));

            String nombreFinal;
            // Si hay tipo y ciudad poner en nombre (Ej: Accidente en Bilbao)
            // Si hay tipo pero no hay ciudad poner en nombre (Ej: Accidente en AP-1)
            // Si no hay ni tipo ni nombre recoger incidenceName y el tipo de incidencia sera "Otras Incidencias"
            if (tipo != null && !tipo.equals("null") && !tipo.isEmpty()) {
                if (ciudad != null && !ciudad.equals("null") && !tipo.isEmpty()){
                    nombreFinal = tipo + " en " + ciudad;
                }else{
                    nombreFinal = tipo + " en " + carretera;
                }
            } else {
                // Intentamos obtener incidenceName como plan B
                Object incidenceName = data.get("incidenceName");
                tipo = "Otras incidencias";
                nombreFinal = (incidenceName != null) ? String.valueOf(incidenceName) : "Incidencia en " + ciudad;
            }

            // Lógica para los tipos de incidencia (Obras, obra y OBRA que sean de mismo formato)
            if (tipo.equalsIgnoreCase("Obras") || tipo.equalsIgnoreCase("OBRA")){
                tipo = "Obras";
            } else if (tipo.equalsIgnoreCase("EVEN") || tipo.equalsIgnoreCase("Evento")) {
                tipo = "Evento";
            } else if (tipo.equalsIgnoreCase("Otras incidencias") || tipo.equalsIgnoreCase("OTRO") || tipo.equalsIgnoreCase("Otros")) {
                tipo = "Otras incidencias";
            }

            // Recuperar latitud y longitud
            String lat = String.valueOf(data.get("latitude"));
            String lon = String.valueOf(data.get("longitude"));
            // Recuperar las fechas de inicio y fin (si tiene)
            String fechaInicioStr = String.valueOf(data.get("startDate"));
            String fechaFinStr = String.valueOf(data.get("endDate"));

            // Crear objeto incidencia con los datos recuperados
            Incidencia inc = new Incidencia();
            inc.setNombre(nombreFinal);
            inc.setTipoIncidencia(tipo);
            inc.setCausa(causa);
            inc.setLatitud(lat);
            inc.setLongitud(lon);
            inc.setUsuario(gobierno);

            // Formatear la fecha inicio con el metodo parseador
            if (fechaInicioStr != null && !fechaInicioStr.equals("null") && !fechaInicioStr.isEmpty()) {
                inc.setFecha_inicio(parsearFechaFlexible(fechaInicioStr));
            }

            // Formatear la fecha fin (si hay) con el metodo parseador
            if (fechaFinStr != null && !fechaFinStr.equals("null") && !fechaFinStr.isEmpty()) {
                inc.setFecha_fin(parsearFechaFlexible(fechaFinStr));
            }

            return inc;
        } catch (Exception e) {
            System.err.println("Error mapeando incidencia: " + e.getMessage());
            return null;
        }
    }

    private Date parsearFechaFlexible(String fechaStr) {
        // Intentamos primero con segundos (2026-01-20T08:00:00)
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaStr);
        } catch (ParseException e) {
            // Si falla, intentamos sin segundos (2024-11-25T00:00)
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(fechaStr);
            } catch (ParseException e2) {
                System.err.println("No se pudo parsear la fecha: " + fechaStr);
                return null;
            }
        }
    }

    private void actualizarIncidenciaExistente(Incidencia existente, Incidencia datosNuevos) {
        // Actualizamos los campos básicos que pueden haber cambiado
        existente.setNombre(datosNuevos.getNombre());
        existente.setTipoIncidencia(datosNuevos.getTipoIncidencia());
        existente.setCausa(datosNuevos.getCausa());
        existente.setLatitud(datosNuevos.getLatitud());
        existente.setLongitud(datosNuevos.getLongitud());

        // Las fechas son críticas: la de inicio no suele cambiar, pero la de fin sí
        existente.setFecha_inicio(datosNuevos.getFecha_inicio());
        existente.setFecha_fin(datosNuevos.getFecha_fin());

    }
}