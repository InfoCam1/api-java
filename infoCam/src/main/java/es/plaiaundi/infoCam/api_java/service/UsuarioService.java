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

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario u) {
        return usuarioRepository.save(u);
    }

    public Usuario registrar(Usuario u) {
        return usuarioRepository.save(u);
    }

    public Usuario login(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void toggleFavoritoCamara(Long userId, Integer camaraId) {
        Optional<Usuario> uOpt = usuarioRepository.findById(userId);
        Optional<Camara> cOpt = camaraRepository.findById(camaraId);

        if (uOpt.isPresent() && cOpt.isPresent()) {
            Usuario u = uOpt.get();
            Camara c = cOpt.get();

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
