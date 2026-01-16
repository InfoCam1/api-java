package es.plaiaundi.infoCam.api_java.config;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CamaraRepository camaraRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final RestTemplate restTemplate;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      CamaraRepository camaraRepository,
                      IncidenciaRepository incidenciaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.camaraRepository = camaraRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. SEEDER DE USUARIOS
        if (usuarioRepository.count() == 0) {
            System.out.println("Cargando datos iniciales de Usuarios...");

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setIs_admin(true);
            admin.setEmail("admin@euskadi.eus");
            admin.setNombre("Administrador");
            usuarioRepository.save(admin);

            Usuario user = new Usuario();
            user.setUsername("erika");
            user.setPassword("123");
            user.setIs_admin(false);
            user.setEmail("erika@email.com");
            user.setNombre("Erika");
            user.setApellido("Martin");
            usuarioRepository.save(user);
        }

        // 2. SEEDER DE CÁMARAS (API EXTERNA TRAFIKOA)
        if (camaraRepository.count() == 0) {
            System.out.println("Iniciando carga directa de cámaras...");
            // Saltarse validación SSL de forma global para el arranque (Truco rápido)
            configurarSslInseguroGlobal();

            String urlBase = "https://api.euskadi.eus/traffic/v1.0/cameras?_page=";

            try {
                for (int i = 1; i <= 25; i++) {
                    // Obtenemos el JSON completo como un Map genérico
                    Map<String, Object> respuesta = restTemplate.getForObject(urlBase + i, Map.class);
                    List<Map<String, Object>> listaCamaras = (List<Map<String, Object>>) respuesta.get("cameras");

                    for (Map<String, Object> cam : listaCamaras) {
                        // Extracción directa de variables según tu formato
                        String nombre = String.valueOf(cam.get("cameraName"));
                        String urlImg = String.valueOf(cam.get("urlImage"));
                        double lat = Double.parseDouble(String.valueOf(cam.get("latitude")));
                        double lon = Double.parseDouble(String.valueOf(cam.get("longitude")));

                        // Aplicar tus condiciones de negocio
                        boolean activa = false;
                        if (urlImg != null && !urlImg.equals("null")) {
                            if (urlImg.contains("www.trafikoa.eus")) {
                                urlImg = urlImg.replace("www.trafikoa.eus", "apps.trafikoa.euskadi.eus");
                                activa = true;
                            } else {
                                activa = verificarImagen(urlImg);
                            }
                        }

                        camaraRepository.save(new Camara(nombre, lat, lon, urlImg, activa));
                    }
                    System.out.println("Página " + i + " procesada.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        // 3. SEEDER DE INCIDENCIAS
        if (incidenciaRepository.count() == 0) {
            System.out.println("Cargando datos iniciales de Incidencias...");
            Incidencia i1 = new Incidencia();
            i1.setNombre("Accidente AP-8");
            i1.setTipoIncidencia("Accidente");
            i1.setCausa("Colisión por alcance");
            i1.setLatitud("43.2");
            i1.setLongitud("-2.9");
            i1.setFecha_inicio(new Date());
            incidenciaRepository.save(i1);

            Incidencia i2 = new Incidencia();
            i2.setNombre("Obras N-1");
            i2.setTipoIncidencia("Obras");
            i2.setCausa("Mantenimiento");
            i2.setLatitud("43.1");
            i2.setLongitud("-2.5");
            i2.setFecha_inicio(new Date());
            incidenciaRepository.save(i2);
        }
    }

    // Método para saltar el error de certificado sin dependencias nuevas
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
    }

    private boolean verificarImagen(String url) {
        try {
            return restTemplate.execute(url, HttpMethod.HEAD, null, r -> r.getStatusCode().is2xxSuccessful());
        } catch (Exception e) {
            return false;
        }
    }
}