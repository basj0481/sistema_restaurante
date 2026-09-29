package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

/** Mesa fisica con su codigo QR (Ver CU12 Ver Menu, CU13 Llamar al Mesero). */
@Entity
@Table(name = "mesas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(name = "codigo_qr", nullable = false, unique = true, length = 60)
    private String codigoQr;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
}
