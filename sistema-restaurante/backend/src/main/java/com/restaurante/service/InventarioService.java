package com.restaurante.service;

import com.restaurante.dto.*;
import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.*;
import com.restaurante.repository.InsumoRepository;
import com.restaurante.repository.MovimientoInventarioRepository;
import com.restaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * CU05 Consultar Inventario (Administrador, solo lectura)
 * CU11 Agregar Productos al Inventario (Cocina, entrada / creacion de insumo)
 * RN08: el descuento automatico por pedido se hace desde PedidoService.
 */
@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final BitacoraService bitacoraService;

    public List<InsumoResponse> listar() {
        return insumoRepository.findAllByOrderByNombreAsc().stream().map(InsumoResponse::de).toList();
    }

    public List<MovimientoInventarioResponse> historialDeInsumo(Long insumoId) {
        if (!insumoRepository.existsById(insumoId)) {
            throw new ResourceNotFoundException("No existen registros.");
        }
        return movimientoRepository.findByInsumoIdOrderByFechaDesc(insumoId).stream()
                .map(MovimientoInventarioResponse::de).toList();
    }

    @Transactional
    public InsumoResponse registrarEntrada(RegistrarEntradaInventarioRequest request, Authentication auth) {
        exigirRolCocina(auth);
        Insumo insumo = insumoRepository.findById(request.insumoId())
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        insumo.setStockActual(insumo.getStockActual().add(request.cantidad()));
        recalcularStockBajo(insumo);
        insumoRepository.save(insumo);

        Usuario cocina = usuarioActual(auth);
        movimientoRepository.save(MovimientoInventario.builder()
                .insumo(insumo)
                .tipo(TipoMovimientoInventario.ENTRADA)
                .cantidad(request.cantidad())
                .usuario(cocina)
                .motivo(request.motivo())
                .build());

        bitacoraService.registrarTransaccion(cocina, "ENTRADA_INVENTARIO",
                "Entrada de " + request.cantidad() + " " + insumo.getUnidadMedida() + " de \"" + insumo.getNombre() + "\".");

        return InsumoResponse.de(insumo);
    }

    @Transactional
    public InsumoResponse crearInsumo(CrearInsumoRequest request, Authentication auth) {
        exigirRolCocina(auth);
        if (insumoRepository.findByNombreIgnoreCase(request.nombre()).isPresent()) {
            throw new BusinessException("Ya existe un insumo con ese nombre.");
        }

        Insumo insumo = Insumo.builder()
                .nombre(request.nombre())
                .unidadMedida(request.unidadMedida())
                .stockActual(request.stockInicial())
                .stockMinimo(request.stockMinimo())
                .build();
        recalcularStockBajo(insumo);
        insumo = insumoRepository.save(insumo);

        Usuario cocina = usuarioActual(auth);
        movimientoRepository.save(MovimientoInventario.builder()
                .insumo(insumo)
                .tipo(TipoMovimientoInventario.CREACION_INSUMO)
                .cantidad(request.stockInicial())
                .usuario(cocina)
                .motivo("Creacion de insumo con stock inicial.")
                .build());

        bitacoraService.registrarTransaccion(cocina, "CREAR_INSUMO",
                "Se creo el insumo \"" + insumo.getNombre() + "\" con stock inicial de " + request.stockInicial() + ".");

        return InsumoResponse.de(insumo);
    }

    /** Usado por PedidoService para el descuento automatico (RN08) y su posible restitucion. */
    @Transactional
    public void ajustarStock(Insumo insumo, BigDecimal delta, TipoMovimientoInventario tipo,
                              Usuario usuario, Pedido pedido, String motivo) {
        insumo.setStockActual(insumo.getStockActual().add(delta));
        recalcularStockBajo(insumo);
        insumoRepository.save(insumo);

        movimientoRepository.save(MovimientoInventario.builder()
                .insumo(insumo)
                .tipo(tipo)
                .cantidad(delta.abs())
                .usuario(usuario)
                .pedido(pedido)
                .motivo(motivo)
                .build());
    }

    private void recalcularStockBajo(Insumo insumo) {
        insumo.setStockBajo(insumo.getStockActual().compareTo(insumo.getStockMinimo()) <= 0);
    }

    private Usuario usuarioActual(Authentication auth) {
        return ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
    }

    /** CU05: el Administrador solo tiene lectura; unicamente Cocina puede registrar movimientos (CU11). */
    private void exigirRolCocina(Authentication auth) {
        Usuario usuario = usuarioActual(auth);
        if (usuario.getRol() != Rol.COCINA) {
            throw new BusinessException(
                    "El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
        }
    }
}
