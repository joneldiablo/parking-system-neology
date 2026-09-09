package com.neology.parking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstanciaDTO {

    @NotBlank(message = "La placa es requerida")
    private String placa;
}
