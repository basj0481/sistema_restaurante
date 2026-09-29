package com.restaurante.repository;

import com.restaurante.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    Optional<Insumo> findByNombreIgnoreCase(String nombre);
    List<Insumo> findAllByOrderByNombreAsc();
    List<Insumo> findByStockBajoTrue();
}
