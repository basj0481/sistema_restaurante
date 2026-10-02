package com.restaurante.service;

import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.Mesa;
import com.restaurante.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CU12 Ver Menu (vía Codigo QR). El contenido del menu en si es un PDF servido por
 * MenuPdfService (mismo para todas las mesas); este servicio solo valida que el
 * codigo QR escaneado corresponda a una mesa activa y devuelve su numero, para
 * mostrarlo en pantalla y para CU13 Llamar al Mesero.
 */
@Service
@RequiredArgsConstructor
public class MenuPublicoService {

    private final MesaRepository mesaRepository;

    public Integer obtenerNumeroMesaPorQr(String codigoQr) {
        Mesa mesa = mesaRepository.findByCodigoQrAndActivaTrue(codigoQr)
                .orElseThrow(() -> new ResourceNotFoundException("El codigo QR no es valido o la mesa no esta activa."));
        return mesa.getNumero();
    }
}
