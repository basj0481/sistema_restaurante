package com.restaurante.controller;

import com.restaurante.dto.*;
import com.restaurante.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU02 Registrar Usuario (solo Administrador, ver SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request, Authentication auth) {
        return ResponseEntity.ok(usuarioService.crear(request, auth));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> actualizarEstado(@PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoUsuarioRequest request, Authentication auth) {
        return ResponseEntity.ok(usuarioService.actualizarEstado(id, request, auth));
    }
}
