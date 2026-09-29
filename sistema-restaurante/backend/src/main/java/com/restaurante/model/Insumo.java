package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** CU05 Consultar Inventario (Admin, lectura) / CU11 Agregar Productos al Inventario (Cocina). */
@Entity
@Table(name = "insumos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @Column(name = "stock_actual", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockActual;

    @Column(name = "stock_minimo", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockMinimo;

    /** RN07 - true cuando stockActual <= stockMinimo. Se recalcula en cada movimiento. */
    @Column(name = "stock_bajo", nullable = false)
    @Builder.Default
    private Boolean stockBajo = false;
}
