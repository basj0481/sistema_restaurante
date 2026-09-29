package com.restaurante.repository;

import com.restaurante.model.CategoriaMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaMenuRepository extends JpaRepository<CategoriaMenu, Long> {
}
