package com.neology.parking.controller;

import com.neology.parking.dto.LoginRequestDTO;
import com.neology.parking.dto.LoginResponseDTO;
import com.neology.parking.dto.MensajeResponseDTO;
import com.neology.parking.dto.UsuarioResponseDTO;
import com.neology.parking.model.Usuario;
import com.neology.parking.repository.UsuarioRepository;
import com.neology.parking.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login JWT y datos de la sesión actual")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener JWT (id cifrado dentro del token)")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        String username = request.username().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.password()));
        } catch (BadCredentialsException | DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeResponseDTO("Credenciales inválidas o usuario inactivo", false));
        }

        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        String token = jwtService.createToken(usuario.getId());
        return ResponseEntity.ok(new LoginResponseDTO(token, usuario.getUsername(), usuario.getRol().name()));
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario autenticado")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }
}