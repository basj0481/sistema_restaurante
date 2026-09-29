package com.restaurante.repository;

import com.restaurante.model.EstadoLlamado;
import com.restaurante.model.LlamadoMesero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LlamadoMeseroRepository extends JpaRepository<LlamadoMesero, Long> {
    List<LlamadoMesero> findByEstadoOrderByFechaCreacionAsc(EstadoLlamado estado);
    Optional<LlamadoMesero> findFirstByMesaIdAndEstadoAndFechaCreacionAfter(Long mesaId, EstadoLlamado estado, LocalDateTime after);
}
