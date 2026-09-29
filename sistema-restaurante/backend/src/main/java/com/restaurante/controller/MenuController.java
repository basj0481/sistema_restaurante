package com.restaurante.controller;

import com.restaurante.dto.*;
import com.restaurante.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Mantenimiento del contenido del menu (categorias y platillos) que alimenta CU12 Ver Menu.
 * Solo Administrador (ver SecurityConfig). No corresponde a un CU formal del alcance actual;
 * ver la nota en MenuService.
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(menuService.listarCategorias());
    }

    @GetMapping("/platillos")
    public ResponseEntity<List<PlatilloResponse>> listarPlatillos() {
        return ResponseEntity.ok(menuService.listarPlatillos());
    }

    @PostMapping("/platillos")
    public ResponseEntity<PlatilloResponse> crear(@Valid @RequestBody PlatilloRequest request, Authentication auth) {
        return ResponseEntity.ok(menuService.crearPlatillo(request, auth));
    }

    @PutMapping("/platillos/{id}")
    public ResponseEntity<PlatilloResponse> actualizar(@PathVariable Long id, @Valid @RequestBody PlatilloRequest request,
                                                         Authentication auth) {
        return ResponseEntity.ok(menuService.actualizarPlatillo(id, request, auth));
    }

    @PatchMapping("/platillos/{id}/disponibilidad")
    public ResponseEntity<PlatilloResponse> disponibilidad(@PathVariable Long id, @RequestBody Map<String, Boolean> body,
                                                             Authentication auth) {
        return ResponseEntity.ok(menuService.cambiarDisponibilidad(id, Boolean.TRUE.equals(body.get("disponible")), auth));
    }
}
