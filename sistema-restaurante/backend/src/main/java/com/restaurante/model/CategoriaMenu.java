package com.restaurante.model;

import jakarta.persistence.*;
import lombok.*;

/** RN02 - Categorias del menu (Entradas, Platos Fuertes, Bebidas, Postres, Combos). */
@Entity
@Table(name = "categorias_menu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false)
    @Builder.Default
    private Integer orden = 0;
}
