package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    // --- Endpoints de Administración ---

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.obtenerTodos();
    }

    @PostMapping
    public Usuario create(@RequestBody Usuario u) {
        return usuarioService.guardar(u);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }

    // --- Endpoints Públicos / Usuario ---

    @GetMapping("/{id}")
    public Usuario getOne(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    @PutMapping("/{id}")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario uInfo) {
        Usuario existente = usuarioService.findById(id);
        if (existente != null) {
            if(uInfo.getNombre() != null) existente.setNombre(uInfo.getNombre());
            if(uInfo.getApellido() != null) existente.setApellido(uInfo.getApellido());
            if(uInfo.getEmail() != null) existente.setEmail(uInfo.getEmail());
            if(uInfo.getTelefono() != 0) existente.setTelefono(uInfo.getTelefono());
            if(uInfo.getPassword() != null) existente.setPassword(uInfo.getPassword());
            return usuarioService.guardar(existente);
        }
        return null;
    }

    @GetMapping("/{id}/favoritos/camaras")
    public Set<Camara> getFavCamaras(@PathVariable Long id) {
        Usuario u = usuarioService.findById(id);
        return (u != null) ? u.getFavoritos() : null;
    }
}
