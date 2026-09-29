package com.restaurante.controller;

import com.restaurante.dto.CobrarCuentaRequest;
import com.restaurante.dto.PagoResponse;
import com.restaurante.service.CobroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** CU08 Cobrar Cuenta (Mesero, Efectivo o Tarjeta). */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class CobroController {

    private final CobroService cobroService;

    @PostMapping("/{id}/cobrar")
    public ResponseEntity<PagoResponse> cobrar(@PathVariable Long id, @Valid @RequestBody CobrarCuentaRequest request,
                                                Authentication auth) {
        return ResponseEntity.ok(cobroService.cobrar(id, request, auth));
    }
}
