package es.plaiaundi.infoCam.api_java.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String remitente;

    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Bienvenido a InfoCam");

            // Cuerpo del mensaje (puedes usar HTML)
            String contenidoHtml = "<h1>Hola " + nombreUsuario + "</h1>" +
                    "<p>Tu cuenta ha sido creada con éxito en <b>InfoCam</b>.</p>" +
                    "<p>¡Gracias por unirte a nuestra comunidad!</p>";

            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            System.out.println("[Email] Correo enviado correctamente a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("[Email] Error al enviar correo: " + e.getMessage());
        }
    }
}