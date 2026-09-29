package com.restaurante.service;

import com.restaurante.dto.*;
import com.restaurante.model.Usuario;
import com.restaurante.repository.UsuarioRepository;
import com.restaurante.security.JwtUtil;
import com.restaurante.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/** CU01 Iniciar Sesion, CU03 Recuperar Contrasena. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BitacoraService bitacoraService;
    private final EmailService emailService;

    @Value("${app.seguridad.max-intentos-fallidos}")
    private int maxIntentosFallidos;

    @Value("${app.reset-password.expiracion-minutos}")
    private int expiracionResetMinutos;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contrasena incorrectos"));

        // FA02: cuenta inactiva o bloqueada
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new LockedException("La cuenta se encuentra inactiva");
        }

        // FA01 / FA03: credenciales invalidas / 5 intentos fallidos
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= maxIntentosFallidos) {
                usuario.setActivo(false);
                bitacoraService.registrarUsuario(usuario, "BLOQUEO_AUTOMATICO",
                        "Cuenta bloqueada tras " + maxIntentosFallidos + " intentos fallidos consecutivos.");
            }
            usuarioRepository.save(usuario);
            throw new BadCredentialsException("Usuario o contrasena incorrectos");
        }

        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        bitacoraService.registrarUsuario(usuario, "INICIO_SESION", "Inicio de sesion exitoso.");

        String token = jwtUtil.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getRol().name());
        return new LoginResponse(token, usuario.getId(), usuario.getNombreCompleto(), usuario.getCorreo(),
                usuario.getRol().name(), usuario.getDebeCambiarPassword());
    }

    @Transactional
    public void solicitarRecuperacion(ForgotPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new BusinessException("El correo no se encuentra registrado."));

        String token = UUID.randomUUID().toString();
        usuario.setTokenReset(token);
        usuario.setTokenResetExpira(LocalDateTime.now().plusMinutes(expiracionResetMinutos));
        usuarioRepository.save(usuario);

        String enlace = frontendBaseUrl + "/restablecer-password?token=" + token;
        emailService.enviarCorreo(usuario.getCorreo(), "Recuperacion de contrasena",
                "Hola " + usuario.getNombreCompleto() + ",\n\n" +
                "Recibimos una solicitud para restablecer tu contrasena. El siguiente enlace vence en " +
                expiracionResetMinutos + " minutos:\n" + enlace +
                "\n\nSi no solicitaste este cambio, ignora este correo.");

        bitacoraService.registrarUsuario(usuario, "SOLICITUD_RESET_PASSWORD",
                "Se genero un enlace de recuperacion de contrasena.");
    }

    @Transactional
    public void restablecerPassword(ResetPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByTokenReset(request.token())
                .orElseThrow(() -> new BusinessException("El enlace de recuperacion de contrasena ha expirado."));

        if (usuario.getTokenResetExpira() == null || usuario.getTokenResetExpira().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El enlace de recuperacion de contrasena ha expirado.");
        }

        validarPassword(request.nuevaPassword());

        usuario.setPasswordHash(passwordEncoder.encode(request.nuevaPassword()));
        usuario.setDebeCambiarPassword(false);
        usuario.setTokenReset(null);
        usuario.setTokenResetExpira(null);
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        bitacoraService.registrarUsuario(usuario, "RESET_PASSWORD", "Contrasena actualizada exitosamente.");
    }

    /** RN05 - minimo 8 caracteres, mayuscula, numero y caracter especial. */
    public static void validarPassword(String password) {
        if (password == null || !password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$")) {
            throw new BusinessException("La contrasena no cumple con los requisitos minimos de seguridad.");
        }
    }
}
