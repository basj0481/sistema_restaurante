package com.restaurante.repository;

import com.restaurante.model.Rol;
import com.restaurante.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByTokenReset(String tokenReset);
    List<Usuario> findByRolAndActivoTrue(Rol rol);
    long countByRolAndActivoTrue(Rol rol);
}
