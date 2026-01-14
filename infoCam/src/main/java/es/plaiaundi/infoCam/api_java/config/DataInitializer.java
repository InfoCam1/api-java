package es.plaiaundi.infoCam.api_java.config;

import es.plaiaundi.infoCam.api_java.model.*;
import es.plaiaundi.infoCam.api_java.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UsuarioRepository uRepo, CamaraRepository cRepo, IncidenciaRepository iRepo) {
        return args -> {
            // 1. Crear Camaras
            Camara c1 = new Camara("Camara Centro", 43.3, -1.9, "img1.jpg", true);
            Camara c2 = new Camara("Camara Playa", 43.4, -1.8, "img2.jpg", true);
            cRepo.saveAll(Set.of(c1, c2));

            // 2. Crear Usuario
            Usuario u = new Usuario();
            u.setUsername("admin");
            u.setPassword("1234");
            u.setIs_admin(true);
            u.setEmail("admin@test.com");
            u.setNombre("Admin");
            u.setApellido("User");
            u.setTelefono(666555444);

            // Asignar Favoritos
            Set<Camara> favs = new HashSet<>();
            favs.add(c1);
            favs.add(c2);
            u.setFavoritos(favs);

            uRepo.save(u); // Guardamos usuario y sus relaciones de favoritos

            // 3. Crear Incidencia
            Incidencia inc = new Incidencia();
            inc.setNombre("Accidente Leve");
            inc.setTipoIncidencia("TRAFICO");
            inc.setCausa("Lluvia");
            inc.setFecha_inicio(new Date());
            inc.setLatitud("43.3");
            inc.setLongitud("-1.9");
            inc.setUsuario(u); // Relación con usuario

            iRepo.save(inc);

            System.out.println("--- DATOS INICIALIZADOS CORRECTAMENTE ---");
        };
    }
}