package com.neology.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResidenteDTO {

    private String placa;
    private Double tiempoAcumuladoMinutos;
    private Double montoTotal;
}
