package com.restaurante.service;

import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.Rol;
import com.restaurante.model.Usuario;
import com.restaurante.security.UsuarioPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * CU12 Ver Menu (vía Codigo QR): el menu ahora se muestra al Cliente como un
 * archivo PDF (en vez de una lista de platillos con fotos). El Administrador
 * sube/reemplaza ese PDF desde el modulo de Menu; el archivo se guarda en disco,
 * en la ruta configurada por app.menu.pdf-path.
 */
@Service
@Slf4j
public class MenuPdfService {

    @Value("${app.menu.pdf-path}")
    private String pdfPath;

    public boolean existe() {
        return Files.exists(Path.of(pdfPath));
    }

    public Resource obtener() {
        Path path = Path.of(pdfPath);
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("No se ha configurado el menu en PDF todavia.");
        }
        return new FileSystemResource(path);
    }

    public void guardar(MultipartFile archivo, Authentication auth) {
        exigirRolAdministrador(auth);

        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("Debe adjuntar un archivo PDF.");
        }
        if (!"application/pdf".equalsIgnoreCase(archivo.getContentType())) {
            throw new BusinessException("El archivo debe ser un PDF.");
        }

        try {
            Path path = Path.of(pdfPath);
            Files.createDirectories(path.getParent());
            archivo.transferTo(path);
            log.info("Menu en PDF actualizado: {} ({} bytes)", path, archivo.getSize());
        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el archivo del menu.");
        }
    }

    private void exigirRolAdministrador(Authentication auth) {
        Usuario usuario = ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new BusinessException(
                    "El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
        }
    }
}
