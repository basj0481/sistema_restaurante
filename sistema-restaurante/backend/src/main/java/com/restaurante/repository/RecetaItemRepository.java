package com.restaurante.repository;

import com.restaurante.model.RecetaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaItemRepository extends JpaRepository<RecetaItem, Long> {
    List<RecetaItem> findByPlatilloId(Long platilloId);
}
