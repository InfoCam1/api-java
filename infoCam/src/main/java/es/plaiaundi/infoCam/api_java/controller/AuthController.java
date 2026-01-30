package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Usuario;
import es.plaiaundi.infoCam.api_java.service.EmailService;
import es.plaiaundi.infoCam.api_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private EmailService emailService;

    //Enpont para registrar un nuevo usuario
    //Se utilizará únicamente desde la aplicación de Móvil
    @PostMapping("/registro")
    public Usuario registro(@RequestBody Usuario usuario) {
        // 1. Lógica de guardado en base de datos
        Usuario guardado = usuarioService.guardar(usuario);

        // 2. Enviar email si el registro fue exitoso
        if (guardado != null && guardado.getEmail() != null) {
            System.out.println("entra");
            emailService.enviarCorreoBienvenida(guardado.getEmail(), guardado.getNombre());
        }

        return guardado;
    }

    //Enpoint para ver si el usuario y contraseña son existentes en la BBDD
    //Se utilizará para inciar sesion tanto en Móvil como en Escritorio
    @PostMapping("/login")
    public Usuario login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");
        return usuarioService.login(username, password);
    }
}
