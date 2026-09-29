package com.restaurante.repository;

import com.restaurante.model.EstadoPedido;
import com.restaurante.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<EstadoPedido> estados);
    List<Pedido> findByMeseroIdAndEstadoInOrderByFechaCreacionDesc(Long meseroId, List<EstadoPedido> estados);
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(EstadoPedido estado);
    List<Pedido> findByEstadoAndFechaCobradoBetween(EstadoPedido estado, LocalDateTime desde, LocalDateTime hasta);
}
