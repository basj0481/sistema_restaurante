package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** CU04 Consultar Bitacora del Sistema. RN06: se escribe automaticamente, nunca se edita/borra. */
@Entity
@Table(name = "bitacora")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBitacora tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String accion;

    @Column(length = 500)
    private String detalle;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
}
