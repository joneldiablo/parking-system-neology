package com.neology.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {

    @NotBlank(message = "La placa es requerida")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Formato de placa inválido")
    private String placa;

    private String tipo;
}
