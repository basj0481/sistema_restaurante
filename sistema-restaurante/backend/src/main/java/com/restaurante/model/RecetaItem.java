package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** RN08 - Insumos y cantidades que se descuentan del inventario al vender un platillo. */
@Entity
@Table(name = "receta_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecetaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platillo_id", nullable = false)
    private Platillo platillo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
}
