package com.restaurante.repository;

import com.restaurante.model.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanillaRepository extends JpaRepository<Planilla, Long> {
    Optional<Planilla> findByUsuarioId(Long usuarioId);
}
