package com.restaurante.security;

import com.restaurante.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalTime;

/**
 * CU01 Iniciar Sesion, FA04: "El sistema cierra sesion al usuario dependiendo su horario."
 * En un esquema JWT sin estado, esto se aplica revalidando en CADA peticion si la hora
 * actual sigue dentro del horario laboral (Ver CU02 "Horario de trabajo") del usuario del
 * token. Si esta fuera de su horario, la peticion sigue sin autenticar: Spring Security la
 * rechaza con 401 y el interceptor del frontend interpreta eso como fin de sesion (logout).
 * Si el usuario no tiene horario configurado (ambos campos null), no aplica esta restriccion.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.esValido(token)) {
                String correo = jwtUtil.obtenerCorreo(token);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = usuarioDetailsService.loadUserByUsername(correo);
                    Usuario usuario = ((UsuarioPrincipal) userDetails).getUsuario();

                    if (dentroDeHorarioLaboral(usuario)) {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                    // Fuera de horario: no se autentica -> la peticion sigue como anonima y
                    // Spring Security la rechazara en los endpoints protegidos (CU01 FA04).
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean dentroDeHorarioLaboral(Usuario usuario) {
        LocalTime inicio = usuario.getHoraInicioTrabajo();
        LocalTime fin = usuario.getHoraFinTrabajo();
        if (inicio == null || fin == null) {
            return true; // sin horario configurado -> sin restriccion
        }
        LocalTime ahora = LocalTime.now();
        if (inicio.isBefore(fin)) {
            return !ahora.isBefore(inicio) && !ahora.isAfter(fin);
        }
        // Turno que cruza medianoche (ej. 22:00 - 06:00)
        return !ahora.isBefore(inicio) || !ahora.isAfter(fin);
    }
}
