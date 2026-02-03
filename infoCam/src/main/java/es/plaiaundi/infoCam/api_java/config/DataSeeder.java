package es.plaiaundi.infoCam.api_java.config;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import jakarta.mail.Message;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CamaraRepository camaraRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final RestTemplate restTemplate;
    private final IncidenciaSyncService incidenciaSyncService;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      CamaraRepository camaraRepository,
                      IncidenciaRepository incidenciaRepository, IncidenciaSyncService incidenciaSyncService) {
        this.usuarioRepository = usuarioRepository;
        this.camaraRepository = camaraRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.incidenciaSyncService = incidenciaSyncService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. SEEDER DE USUARIOS
        // creacion de un usuario especial que será el creador de las incidencias
        Usuario gobierno = new Usuario();

        // verificar que no haya usuarios insertados ya
        if (usuarioRepository.count() == 0) {
            System.out.println("Cargando datos iniciales de Usuarios...");

            // Usuario de gobierno que será creador de incidencias de OpenData
            gobierno.setUsername("gov");
            gobierno.setPassword(hash("123"));
            gobierno.setIs_admin(true);
            gobierno.setEmail("gab-lehendak@euskadi.eus");
            gobierno.setNombre("Gobierno");
            gobierno.setApellido("Vasco");
            gobierno.setTelefono(666777888);
            usuarioRepository.save(gobierno);

            // Usuario admin
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(hash("admin123"));
            admin.setIs_admin(true);
            admin.setEmail("admin@euskadi.eus");
            admin.setNombre("Administrador");
            admin.setTelefono(696969696);
            usuarioRepository.save(admin);

            // Usuario normal
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(hash("123"));
            user.setIs_admin(false);
            user.setEmail("user@email.com");
            user.setNombre("User");
            user.setApellido("User");
            user.setTelefono(123456789);
            usuarioRepository.save(user);

            System.out.println("\n====================================================");
            System.out.println("   VERIFICACIÓN DE USUARIOS CARGADOS EN SISTEMA");
            System.out.println("====================================================");

            // Recuperamos todos para asegurar que el save() funcionó
            usuarioRepository.findAll().forEach(u -> {
                System.out.printf("ID: %-4d | Username: %-10s | Rol: %-10s | Email: %s%n",
                        u.getId(),
                        u.getUsername(),
                        u.getIs_admin() ? "ADMIN" : "USER",
                        u.getEmail());
            });

            System.out.println("====================================================\n");
        }

        // 2. SEEDER DE CÁMARAS
        if (camaraRepository.count() == 0) {
            System.out.println("\n>>> INICIANDO CARGA DIRECTA DE CÁMARAS (OpenData Euskadi)...");
            configurarSslInseguroGlobal();

            // Contadores para el resumen final
            int insertadas = 0;
            int fallidas = 0;
            long tiempoInicio = System.currentTimeMillis();

            String urlBase = "https://api.euskadi.eus/traffic/v1.0/cameras?_page=";

            try {
                for (int i = 1; i <= 25; i++) {
                    Map<String, Object> respuesta = restTemplate.getForObject(urlBase + i, Map.class);
                    if (respuesta == null) continue;

                    List<Map<String, Object>> listaCamaras = (List<Map<String, Object>>) respuesta.get("cameras");

                    for (Map<String, Object> cam : listaCamaras) {
                        try {
                            String nombre = String.valueOf(cam.get("cameraName"));
                            String urlImg = String.valueOf(cam.get("urlImage"));
                            double lat = Double.parseDouble(String.valueOf(cam.get("latitude")));
                            double lon = Double.parseDouble(String.valueOf(cam.get("longitude")));

                            boolean activa = false;
                            if (urlImg != null && !urlImg.equals("null") && !urlImg.isEmpty()) {
                                if (urlImg.contains("www.trafikoa.eus")) {
                                    urlImg = urlImg.replace("www.trafikoa.eus", "apps.trafikoa.euskadi.eus");
                                    activa = true;
                                } else {
                                    activa = verificarImagen(urlImg);
                                }
                            }

                            camaraRepository.save(new Camara(nombre, lat, lon, urlImg, activa));

                            // Log individual (opcional: puedes comentarlo si hay demasiadas cámaras)
                            System.out.println("[+] CAM: " + nombre + " [" + (activa ? "ACTIVA" : "OFF") + "]");
                            insertadas++;

                        } catch (Exception e) {
                            System.err.println("[-] Error procesando cámara individual: " + e.getMessage());
                            fallidas++;
                        }
                    }
                    System.out.println("--> Página " + i + " completada.");
                }

                // RESUMEN FINAL
                long tiempoFin = System.currentTimeMillis();
                double segundos = (tiempoFin - tiempoInicio) / 1000.0;

                System.out.println("\n====================================================");
                System.out.println("       RESUMEN DE CARGA DE CÁMARAS");
                System.out.println("====================================================");
                System.out.println("Cámaras insertadas correctamente: " + insertadas);
                System.out.println("Cámaras fallidas/ignoradas:       " + fallidas);
                System.out.println("Tiempo total de ejecución:        " + segundos + " segundos");
                System.out.println("====================================================\n");

            } catch (Exception e) {
                System.err.println("!!! ERROR CRÍTICO EN SEEDER DE CÁMARAS: " + e.getMessage());
            }
        } else {
            System.out.println(">>> Cámaras detectadas en BD. Saltando seeder.");
        }

        // 3. SEEDER DE INCIDENCIAS
        // llamar al seeder que inserta/actualiza los datos de las incidencias diariamente
        if (incidenciaRepository.count() == 0) {
            System.out.println("Ejecutando primera carga de incidencias reales...");
            incidenciaSyncService.sincronizarIncidenciasDiarias();
        }
    }

    // Metodo para saltar el error de certificado sin dependencias nuevas
    private void configurarSslInseguroGlobal() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    // Metodo que verifica que la imagen que devuelve OpenData es correcta
    private boolean verificarImagen(String url) {
        try {
            return restTemplate.execute(url, HttpMethod.HEAD, null, r -> r.getStatusCode().is2xxSuccessful());
        } catch (Exception e) {
            return false;
        }
    }

    private String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}