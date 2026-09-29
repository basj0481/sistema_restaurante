package com.restaurante.service;

import com.restaurante.dto.CategoriaResponse;
import com.restaurante.dto.PlatilloResponse;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.Mesa;
import com.restaurante.repository.CategoriaMenuRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PlatilloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** CU12 Ver Menu (vía Codigo QR). Sin autenticacion. */
@Service
@RequiredArgsConstructor
public class MenuPublicoService {

    private final MesaRepository mesaRepository;
    private final CategoriaMenuRepository categoriaRepository;
    private final PlatilloRepository platilloRepository;

    public Map<String, Object> obtenerMenuPorQr(String codigoQr) {
        Mesa mesa = mesaRepository.findByCodigoQrAndActivaTrue(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("El codigo QR no es valido o la mesa no esta activa."));

        List<CategoriaResponse> categorias = categoriaRepository.findAll().stream()
                .map(CategoriaResponse::de).toList();
        List<PlatilloResponse> platillos = platilloRepository.findAllByOrderByCategoriaIdAscNombreAsc().stream()
                .map(PlatilloResponse::de).toList();

        return Map.of(
                "numeroMesa", mesa.getNumero(),
                "categorias", categorias,
                "platillos", platillos
        );
    }
}
