package com.restaurante.controller;

import com.restaurante.dto.*;
import com.restaurante.service.MenuPdfService;
import com.restaurante.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Mantenimiento del contenido del menu (categorias, platillos y PDF publico) que
 * alimenta CU07 Generar Pedido (platillos, para el Mesero) y CU12 Ver Menu (PDF,
 * para el Cliente). Solo Administrador (ver SecurityConfig). Los platillos/categorias
 * no corresponden a un CU formal del alcance actual; ver la nota en MenuService.
 */
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final MenuPdfService menuPdfService;

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

    /** CU12: el Administrador sube/reemplaza el PDF del menu que ve el Cliente. */
    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subirMenuPdf(@RequestParam("archivo") MultipartFile archivo,
                                                              Authentication auth) {
        menuPdfService.guardar(archivo, auth);
        return ResponseEntity.ok(Map.of("mensaje", "Menu en PDF actualizado exitosamente."));
    }

    /** Vista previa del PDF vigente para el Administrador (mismo archivo que ve el Cliente en CU12). */
    @GetMapping("/pdf")
    public ResponseEntity<Resource> obtenerMenuPdf() {
        Resource recurso = menuPdfService.obtener();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"menu.pdf\"")
                .body(recurso);
    }
}

