package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CU02 Registrar Usuario, campo "h. Salario (almacenado en una tabla de base de
 * datos llamada 'planilla')". Relacion 1:1 con Usuario, en su propia tabla.
 */
@Entity
@Table(name = "planilla")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Planilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(name = "fecha_actualizacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}
