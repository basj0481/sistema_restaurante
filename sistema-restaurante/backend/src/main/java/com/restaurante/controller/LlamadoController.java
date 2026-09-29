package com.restaurante.controller;

import com.restaurante.dto.LlamadoResponse;
import com.restaurante.service.LlamadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU13 Llamar al Mesero - lado del Mesero (crear el llamado es publico, ver PublicoController). */
@RestController
@RequestMapping("/api/llamados")
@RequiredArgsConstructor
public class LlamadoController {

    private final LlamadoService llamadoService;

    @GetMapping
    public ResponseEntity<List<LlamadoResponse>> pendientes() {
        return ResponseEntity.ok(llamadoService.listarPendientes());
    }

    @PatchMapping("/{id}/atender")
    public ResponseEntity<LlamadoResponse> atender(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(llamadoService.atender(id, auth));
    }
}
