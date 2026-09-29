package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** CU13 Llamar al Mesero (vía QR). */
@Entity
@Table(name = "llamados_mesero")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LlamadoMesero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoLlamado estado = EstadoLlamado.PENDIENTE;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_atendido")
    private LocalDateTime fechaAtendido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendido_por")
    private Usuario atendidoPor;
}
