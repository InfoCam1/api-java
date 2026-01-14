package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario u) {
        return usuarioRepository.save(u);
    }
}