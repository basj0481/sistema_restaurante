package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** CU12 Ver Menu. Los platillos y su receta se administran desde el modulo de Menu. */
@Entity
@Table(name = "platillos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Platillo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaMenu categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "foto_url", length = 300)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPlatillo estado = EstadoPlatillo.DISPONIBLE;

    @OneToMany(mappedBy = "platillo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecetaItem> receta = new ArrayList<>();
}
