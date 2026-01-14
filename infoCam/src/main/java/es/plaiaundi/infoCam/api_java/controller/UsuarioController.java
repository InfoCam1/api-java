package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.obtenerTodos();
    }
}