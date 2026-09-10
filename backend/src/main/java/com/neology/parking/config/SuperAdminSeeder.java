package com.neology.parking.config;

import com.neology.parking.model.Usuario;
import com.neology.parking.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea/sincroniza el SUPERADMIN a partir de las variables de entorno
 * (SUPERADMIN_USER / SUPERADMIN_PASSWORD del .env o del docker-compose).
 * Es la ÚNICA vía para tener un superadmin: no se crea por el CRUD de admins.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.user:}")
    private String superadminUser;

    @Value("${superadmin.password:}")
    private String superadminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (superadminUser == null || superadminUser.isBlank()
                || superadminPassword == null || superadminPassword.isBlank()) {
            log.warn("Superadmin NO configurado: define SUPERADMIN_USER y SUPERADMIN_PASSWORD en el .env");
            return;
        }

        String username = superadminUser.trim().toLowerCase();
        usuarioRepository.findByUsername(username)
                .ifPresentOrElse(
                        usuario -> {
                            usuario.setPassword(passwordEncoder.encode(superadminPassword));
                            usuario.setRol(Usuario.Rol.SUPERADMIN);
                            usuario.setActivo(true);
                            usuarioRepository.save(usuario);
                            log.info("Superadmin '{}' sincronizado desde el .env", username);
                        },
                        () -> {
                            Usuario superadmin = new Usuario(
                                    null,
                                    username,
                                    passwordEncoder.encode(superadminPassword),
                                    Usuario.Rol.SUPERADMIN,
                                    true
                            );
                            usuarioRepository.save(superadmin);
                            log.info("Superadmin '{}' creado desde el .env", username);
                        });
    }
}