package com.restaurante.service;

import com.restaurante.dto.LlamadoResponse;
import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.EstadoLlamado;
import com.restaurante.model.LlamadoMesero;
import com.restaurante.model.Mesa;
import com.restaurante.model.Usuario;
import com.restaurante.repository.LlamadoMeseroRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** CU13 Llamar al Mesero (vía Codigo QR). */
@Service
@RequiredArgsConstructor
public class LlamadoService {

    private static final int MINUTOS_ESPERA_ENTRE_LLAMADOS = 2;

    private final LlamadoMeseroRepository llamadoRepository;
    private final MesaRepository mesaRepository;
    private final BitacoraService bitacoraService;

    @Transactional
    public LlamadoResponse crearLlamado(String codigoQr) {
        Mesa mesa = mesaRepository.findByCodigoQrAndActivaTrue(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("El codigo QR no es valido."));

        // No permitir mas de un llamado activo por mesa (Requerimiento no funcional del CU13)
        LocalDateTime limite = LocalDateTime.now().minusMinutes(MINUTOS_ESPERA_ENTRE_LLAMADOS);
        llamadoRepository.findFirstByMesaIdAndEstadoAndFechaCreacionAfter(mesa.getId(), EstadoLlamado.PENDIENTE, limite)
                .ifPresent(l -> {
                    throw new BusinessException("Ya existe un llamado pendiente para esta mesa. Por favor espere.");
                });

        LlamadoMesero llamado = LlamadoMesero.builder().mesa(mesa).estado(EstadoLlamado.PENDIENTE).build();
        llamado = llamadoRepository.save(llamado);

        bitacoraService.registrarTransaccion(null, "LLAMADO_MESERO",
                "La mesa " + mesa.getNumero() + " solicito atencion.");

        return LlamadoResponse.de(llamado);
    }

    public List<LlamadoResponse> listarPendientes() {
        return llamadoRepository.findByEstadoOrderByFechaCreacionAsc(EstadoLlamado.PENDIENTE)
                .stream().map(LlamadoResponse::de).toList();
    }

    @Transactional
    public LlamadoResponse atender(Long id, Authentication auth) {
        LlamadoMesero llamado = llamadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        Usuario mesero = ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
        llamado.setEstado(EstadoLlamado.ATENDIDO);
        llamado.setFechaAtendido(LocalDateTime.now());
        llamado.setAtendidoPor(mesero);
        llamadoRepository.save(llamado);

        return LlamadoResponse.de(llamado);
    }
}
