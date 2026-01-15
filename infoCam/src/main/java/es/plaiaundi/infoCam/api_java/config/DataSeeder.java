package es.plaiaundi.infoCam.api_java.config;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CamaraRepository camaraRepository;
    private final IncidenciaRepository incidenciaRepository;

    public DataSeeder(UsuarioRepository usuarioRepository, 
                      CamaraRepository camaraRepository, 
                      IncidenciaRepository incidenciaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.camaraRepository = camaraRepository;
        this.incidenciaRepository = incidenciaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
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

        if (camaraRepository.count() == 0) {
            System.out.println("Cargando datos iniciales de Cámaras...");
            // constructor: nombre, lat, lon, imagen, activa
            camaraRepository.save(new Camara("Camara Bilbao Centro", 43.2630, -2.9349, "url_img_1", true));
            camaraRepository.save(new Camara("Camara Donostia", 43.3183, -1.9812, "url_img_2", true));
            camaraRepository.save(new Camara("Camara Vitoria-Gasteiz", 42.8467, -2.6716, "url_img_3", false));
        }

        if (incidenciaRepository.count() == 0) {
            System.out.println("Cargando datos iniciales de Incidencias...");
            Incidencia i1 = new Incidencia();
            i1.setNombre("Accidente AP-8");
            i1.setTipoIncidencia("Accidente");
            i1.setCausa("Colisión por alcance");
            i1.setLatitud("43.2"); // String as per constraint
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
}
