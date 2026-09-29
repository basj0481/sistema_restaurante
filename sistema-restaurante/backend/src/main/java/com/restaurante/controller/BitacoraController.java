package com.restaurante.controller;

import com.restaurante.dto.BitacoraResponse;
import com.restaurante.model.TipoBitacora;
import com.restaurante.service.BitacoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/** CU04 Consultar Bitacora del Sistema (solo Administrador, ver SecurityConfig). */
@RestController
@RequestMapping("/api/bitacora")
@RequiredArgsConstructor
public class BitacoraController {

    private final BitacoraService bitacoraService;

    @GetMapping
    public ResponseEntity<List<BitacoraResponse>> consultar(
            @RequestParam(required = false) TipoBitacora tipo,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(bitacoraService.consultar(tipo, usuarioId, desde, hasta));
    }
}
