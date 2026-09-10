package com.neology.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 50)
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(max = 100)
        String password
) {}