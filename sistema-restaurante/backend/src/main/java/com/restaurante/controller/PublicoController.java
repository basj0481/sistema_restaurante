package com.restaurante.controller;

import com.restaurante.dto.LlamadoResponse;
import com.restaurante.service.LlamadoService;
import com.restaurante.service.MenuPdfService;
import com.restaurante.service.MenuPublicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CU12 Ver Menu (vía Codigo QR) y CU13 Llamar al Mesero (vía Codigo QR).
 * Endpoints publicos, sin autenticacion (ver SecurityConfig: /api/publico/**).
 */
@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoController {

    private final MenuPublicoService menuPublicoService;
    private final MenuPdfService menuPdfService;
    private final LlamadoService llamadoService;

    /** CU12 paso 1-2: el Cliente escanea el QR de su mesa; se valida y se devuelve el numero de mesa. */
    @GetMapping("/mesas/{codigoQr}/menu")
    public ResponseEntity<Map<String, Object>> verMenu(@PathVariable String codigoQr) {
        Integer numeroMesa = menuPublicoService.obtenerNumeroMesaPorQr(codigoQr);
        return ResponseEntity.ok(Map.of(
                "numeroMesa", numeroMesa,
                "menuDisponible", menuPdfService.existe()
        ));
    }

    /** CU12 paso 3: el sistema muestra el menu del restaurante (ahora en PDF). */
    @GetMapping("/menu.pdf")
    public ResponseEntity<Resource> menuPdf() {
        Resource recurso = menuPdfService.obtener();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"menu.pdf\"")
                .body(recurso);
    }

    /** CU13 - el Cliente solicita la atencion de un Mesero desde el menu digital de su mesa. */
    @PostMapping("/mesas/{codigoQr}/llamar-mesero")
    public ResponseEntity<LlamadoResponse> llamarMesero(@PathVariable String codigoQr) {
        return ResponseEntity.ok(llamadoService.crearLlamado(codigoQr));
    }
}
