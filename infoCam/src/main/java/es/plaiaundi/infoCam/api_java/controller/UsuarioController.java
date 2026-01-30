package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.service.EmailService;
import es.plaiaundi.infoCam.api_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private EmailService emailService;

    //Enpoint que devuelve todos los usuarios
    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.obtenerTodos();
    }

    //Enpoint que recibe un objeto usuario y lo inserta en la BBDD
    @PostMapping
    public Usuario create(@RequestBody Usuario u) {
        Usuario guardado = usuarioService.guardar(u);

        // 2. Enviar email si el registro fue exitoso
        if (guardado != null && guardado.getEmail() != null) {
            System.out.println("entra");
            emailService.enviarCorreoBienvenida(guardado.getEmail(), guardado.getNombre());
        }

        return guardado;
    }

    //Enpoint con un id que elimina el registro de la BBDD
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }

    //Enpoint con un id que devuelve el usuario en cuestión
    @GetMapping("/{id}")
    public Usuario getOne(@PathVariable Long id) {
        return usuarioService.findById(id);
    }

    //Enpoint con un id que recibe un objeto usuario que actualiza la BBDD
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

    //Enpoint con un id que lista las cámaras favoritas del usuario en cuestión
    @GetMapping("/{id}/favoritos/camaras")
    public Set<Camara> getFavCamaras(@PathVariable Long id) {
        Usuario u = usuarioService.findById(id);
        return (u != null) ? u.getFavoritos() : null;
    }
}
