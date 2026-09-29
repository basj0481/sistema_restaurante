package com.restaurante.repository;

import com.restaurante.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByCodigoQrAndActivaTrue(String codigoQr);
    Optional<Mesa> findByNumero(Integer numero);
}
