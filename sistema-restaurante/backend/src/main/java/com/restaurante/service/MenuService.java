package com.restaurante.service;

import com.restaurante.dto.*;
import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.security.UsuarioPrincipal;
import com.restaurante.model.*;
import com.restaurante.repository.CategoriaMenuRepository;
import com.restaurante.repository.InsumoRepository;
import com.restaurante.repository.PlatilloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Administracion del contenido del menu (categorias y platillos) que el Cliente
 * consulta en CU12 Ver Menu. No existe un CU dedicado a este mantenimiento en el
 * alcance actual; se expone como una utilidad minima para el Administrador, necesaria
 * para que el menu digital (CU12) tenga contenido que mostrar.
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final CategoriaMenuRepository categoriaRepository;
    private final PlatilloRepository platilloRepository;
    private final InsumoRepository insumoRepository;

    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    public List<PlatilloResponse> listarPlatillos() {
        return platilloRepository.findAllByOrderByCategoriaIdAscNombreAsc().stream()
                .map(PlatilloResponse::de).toList();
    }

    @Transactional
    public PlatilloResponse crearPlatillo(PlatilloRequest request, Authentication auth) {
        exigirRolAdministrador(auth);
        CategoriaMenu categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        Platillo platillo = Platillo.builder()
                .nombre(request.nombre())
                .categoria(categoria)
                .precio(request.precio())
                .descripcion(request.descripcion())
                .fotoUrl(request.fotoUrl())
                .estado(EstadoPlatillo.DISPONIBLE)
                .build();

        aplicarReceta(platillo, request);
        platillo = platilloRepository.save(platillo);
        return PlatilloResponse.de(platillo);
    }

    @Transactional
    public PlatilloResponse actualizarPlatillo(Long id, PlatilloRequest request, Authentication auth) {
        exigirRolAdministrador(auth);
        Platillo platillo = platilloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
        CategoriaMenu categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

        platillo.setNombre(request.nombre());
        platillo.setCategoria(categoria);
        platillo.setPrecio(request.precio());
        platillo.setDescripcion(request.descripcion());
        platillo.setFotoUrl(request.fotoUrl());

        platillo.getReceta().clear();
        aplicarReceta(platillo, request);

        platillo = platilloRepository.save(platillo);
        return PlatilloResponse.de(platillo);
    }

    @Transactional
    public PlatilloResponse cambiarDisponibilidad(Long id, boolean disponible, Authentication auth) {
        exigirRolAdministrador(auth);
        Platillo platillo = platilloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
        platillo.setEstado(disponible ? EstadoPlatillo.DISPONIBLE : EstadoPlatillo.AGOTADO);
        platilloRepository.save(platillo);
        return PlatilloResponse.de(platillo);
    }

    private void exigirRolAdministrador(Authentication auth) {
        Usuario usuario = ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new BusinessException(
                    "El usuario no cuenta con permisos para realizar esta accion, o la cuenta se encuentra inactiva.");
        }
    }

    private void aplicarReceta(Platillo platillo, PlatilloRequest request) {
        List<RecetaItem> receta = new ArrayList<>();
        if (request.receta() != null) {
            for (RecetaItemRequest ri : request.receta()) {
                Insumo insumo = insumoRepository.findById(ri.insumoId())
                        .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
                receta.add(RecetaItem.builder()
                        .platillo(platillo)
                        .insumo(insumo)
                        .cantidad(ri.cantidad())
                        .build());
            }
        }
        platillo.setReceta(receta);
    }
}
