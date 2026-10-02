package com.restaurante.service;

import com.restaurante.dto.*;
import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.Planilla;
import com.restaurante.model.Rol;
import com.restaurante.model.Usuario;
import com.restaurante.repository.PlanillaRepository;
import com.restaurante.repository.UsuarioRepository;
import com.restaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** CU02 Registrar Usuario. Solo el Administrador puede crear/activar/desactivar cuentas. */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PlanillaRepository planillaRepository;
    private final PasswordEncoder passwordEncoder;
    private final BitacoraService bitacoraService;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(u -> UsuarioResponse.de(u, salarioDe(u)))
                .toList();
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request, Authentication auth) {
        // FA03
        if (usuarioRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new BusinessException("El correo electronico ya se encuentra registrado.");
        }
        // FA04 / RN05
        AuthService.validarPassword(request.passwordTemporal());

        Usuario admin = usuarioActual(auth);

        // CU02 campo g. Horario de trabajo (seleccion por hora)
        Usuario nuevo = Usuario.builder()
                .nombreCompleto(request.nombreCompleto())
                .correo(request.correo())
                .telefono(request.telefono())
                .rol(request.rol())
                .passwordHash(passwordEncoder.encode(request.passwordTemporal()))
                .activo(true)
                .debeCambiarPassword(true)
                .intentosFallidos(0)
                .creadoPor(admin.getId())
                .horaInicioTrabajo(request.horaInicioTrabajo())
                .horaFinTrabajo(request.horaFinTrabajo())
                .build();

        nuevo = usuarioRepository.save(nuevo);

        // CU02 campo h. Salario, almacenado en la tabla "planilla" (Ver Planilla.java)
        Planilla planilla = Planilla.builder()
                .usuario(nuevo)
                .salario(request.salario())
                .build();
        planillaRepository.save(planilla);

        bitacoraService.registrarUsuario(admin, "CREAR_USUARIO",
                "Se creo la cuenta \"" + nuevo.getCorreo() + "\" con rol " + nuevo.getRol() + ".");

        return UsuarioResponse.de(nuevo, request.salario());
    }

    @Transactional
    public UsuarioResponse actualizarEstado(Long id, ActualizarEstadoUsuarioRequest request, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        // RN: no se permite desactivar al ultimo Administrador activo
        if (!request.activo() && usuario.getRol() == Rol.ADMINISTRADOR
                && usuarioRepository.countByRolAndActivoTrue(Rol.ADMINISTRADOR) <= 1) {
            throw new BusinessException("No se puede desactivar el ultimo usuario con rol Administrador.");
        }

        usuario.setActivo(request.activo());
        if (request.activo()) {
            usuario.setIntentosFallidos(0);
        }
        usuarioRepository.save(usuario);

        Usuario admin = usuarioActual(auth);
        bitacoraService.registrarUsuario(admin, request.activo() ? "REACTIVAR_USUARIO" : "DESACTIVAR_USUARIO",
                "Cuenta \"" + usuario.getCorreo() + "\" " + (request.activo() ? "reactivada." : "desactivada."));

        return UsuarioResponse.de(usuario, salarioDe(usuario));
    }

    private BigDecimal salarioDe(Usuario usuario) {
        return planillaRepository.findByUsuarioId(usuario.getId())
                .map(Planilla::getSalario)
                .orElse(null);
    }

    private Usuario usuarioActual(Authentication auth) {
        return ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
    }
}
