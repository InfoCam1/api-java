package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CamaraRepository camaraRepository;

    //método jpa para listar todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    //método jpa que recibe un usuario y lo guarda en la BBDD
    public Usuario guardar(Usuario u) {
        return usuarioRepository.save(u);
    }

    //método jpa para registrar un usuario
    public Usuario registrar(Usuario u) {
        return usuarioRepository.save(u);
    }

    //método jpa que recibe usuario y contraseña y comprueba que exista
    public Usuario login(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    //método jpa que busca un usuario por su id
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    //método que recibe un id de usuario y cámara y los pone en favorito
    public void toggleFavoritoCamara(Long userId, Integer camaraId) {
        //verificar que ambos objetos existan en la base de datos
        Optional<Usuario> uOpt = usuarioRepository.findById(userId);
        Optional<Camara> cOpt = camaraRepository.findById(camaraId);

        if (uOpt.isPresent() && cOpt.isPresent()) {
            Usuario u = uOpt.get();
            Camara c = cOpt.get();

            //si el usuario ya tiene esa cámara en favorito lo borra si no lo añade a favoritos
            if (u.getFavoritos().contains(c)) {
                u.getFavoritos().remove(c);
            } else {
                u.getFavoritos().add(c);
            }
            usuarioRepository.save(u);
        }
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
