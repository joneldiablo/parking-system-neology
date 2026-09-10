package com.neology.parking.dto;

import com.neology.parking.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String rol,
        boolean activo
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.isActivo()
        );
    }
}