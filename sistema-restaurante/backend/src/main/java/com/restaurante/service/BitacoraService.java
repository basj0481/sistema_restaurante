package com.restaurante.service;

import com.restaurante.dto.BitacoraResponse;
import com.restaurante.exception.BusinessException;
import com.restaurante.model.Bitacora;
import com.restaurante.model.TipoBitacora;
import com.restaurante.model.Usuario;
import com.restaurante.repository.BitacoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** CU04 Consultar Bitacora del Sistema / RN06: todo evento relevante se registra automaticamente. */
@Service
@RequiredArgsConstructor
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    /** CU04 - consulta con filtros de tipo, usuario y rango de fechas. */
    public List<BitacoraResponse> consultar(TipoBitacora tipo, Long usuarioId, LocalDateTime desde, LocalDateTime hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            // RN AN02 No.13
            throw new BusinessException("El rango de fechas seleccionado para el reporte no es valido.");
        }

        Specification<Bitacora> spec = (root, query, cb) -> cb.conjunction();
        if (tipo != null) {
            Specification<Bitacora> f = (root, query, cb) -> cb.equal(root.get("tipo"), tipo);
            spec = spec.and(f);
        }
        if (usuarioId != null) {
            Specification<Bitacora> f = (root, query, cb) -> cb.equal(root.get("usuario").get("id"), usuarioId);
            spec = spec.and(f);
        }
        if (desde != null) {
            Specification<Bitacora> f = (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), desde);
            spec = spec.and(f);
        }
        if (hasta != null) {
            Specification<Bitacora> f = (root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), hasta);
            spec = spec.and(f);
        }

        List<BitacoraResponse> resultado = new ArrayList<>();
        bitacoraRepository.findAll(spec, org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "fecha"))
                .forEach(b -> resultado.add(BitacoraResponse.de(b)));
        return resultado;
    }

    public void registrarTransaccion(Usuario usuario, String accion, String detalle) {
        registrar(TipoBitacora.TRANSACCION, usuario, accion, detalle);
    }

    public void registrarUsuario(Usuario usuario, String accion, String detalle) {
        registrar(TipoBitacora.USUARIO, usuario, accion, detalle);
    }

    private void registrar(TipoBitacora tipo, Usuario usuario, String accion, String detalle) {
        Bitacora b = Bitacora.builder()
                .tipo(tipo)
                .usuario(usuario)
                .accion(accion)
                .detalle(detalle)
                .build();
        bitacoraRepository.save(b);
    }
}
