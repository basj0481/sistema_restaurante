package com.restaurante.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * CU03 Recuperar Contrasena / CU02 Registrar Usuario.
 * En caso de que el servidor SMTP no este configurado (ambiente de desarrollo),
 * el envio se degrada a un log en consola en lugar de lanzar un error,
 * para no bloquear el flujo funcional del caso de uso.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo a {} (revisar configuracion SMTP). Detalle: {}", destinatario, e.getMessage());
            log.info("=== Contenido del correo que se hubiera enviado ===\nPara: {}\nAsunto: {}\n{}", destinatario, asunto, cuerpo);
        }
    }
}
