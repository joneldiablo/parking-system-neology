package com.neology.parking.dto;

public record LoginResponseDTO(
        String token,
        String username,
        String rol
) {}