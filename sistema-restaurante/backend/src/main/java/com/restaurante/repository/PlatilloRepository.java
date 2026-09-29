package com.restaurante.repository;

import com.restaurante.model.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatilloRepository extends JpaRepository<Platillo, Long> {
    List<Platillo> findAllByOrderByCategoriaIdAscNombreAsc();
}
