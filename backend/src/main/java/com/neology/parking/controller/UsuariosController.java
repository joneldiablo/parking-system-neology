package com.neology.parking.controller;

import com.neology.parking.dto.MensajeResponseDTO;
import com.neology.parking.dto.UsuarioCreateDTO;
import com.neology.parking.dto.UsuarioResponseDTO;
import com.neology.parking.dto.UsuarioUpdateDTO;
import com.neology.parking.model.Usuario;
import com.neology.parking.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Gestión de administradores. SOLO accesible para {@code SUPERADMIN}
 * (regla en {@code SecurityConfig} + {@code @PreAuthorize} como doble candado).
 * Los admins no tienen ningún CRUD sobre la tabla de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "CRUD de admins (solo superadmin)")
public class UsuariosController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Listar todos los administradores")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<UsuarioResponseDTO> usuarios = usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::from)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Obtener un administrador por id")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Crear un administrador (siempre rol ADMIN)")
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        String username = dto.username().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeResponseDTO("Ya existe un usuario con ese nombre", false));
        }
        Usuario nuevo = new Usuario(
                null,
                username,
                passwordEncoder.encode(dto.password()),
                Usuario.Rol.ADMIN,
                dto.activo() == null || dto.activo()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponseDTO.from(usuarioRepository.save(nuevo)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Actualizar admin (contraseña opcional y estado activo)")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody UsuarioUpdateDTO dto,
                                        Authentication authentication) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (dto.password() != null && !dto.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.activo() != null) {
            if (usuario.getUsername().equals(authentication.getName()) && !dto.activo()) {
                return ResponseEntity.badRequest()
                        .body(new MensajeResponseDTO("No puedes desactivar tu propia cuenta", false));
            }
            usuario.setActivo(dto.activo());
        }
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioRepository.save(usuario)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Eliminar un administrador (nunca a ti mismo ni al superadmin)")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuario.getUsername().equals(authentication.getName())) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponseDTO("No puedes eliminar tu propia cuenta", false));
        }
        if (usuario.getRol() == Usuario.Rol.SUPERADMIN) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponseDTO("No puedes eliminar al superadmin", false));
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok(new MensajeResponseDTO("Administrador eliminado", true));
    }
}