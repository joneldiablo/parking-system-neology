package com.neology.parking.dto;

import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
        @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        Boolean activo
) {}