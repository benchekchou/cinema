package org.example.cenima.security;

import lombok.RequiredArgsConstructor;
import org.example.cenima.entity.AppUser;
import org.example.cenima.entity.Role;
import org.example.cenima.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SecurityUserSeeder implements CommandLineRunner {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.auth.seed-enabled:true}")
    private boolean seedEnabled;

    @Value("${security.auth.admin.email:admin@cinema.local}")
    private String adminEmail;

    @Value("${security.auth.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${security.auth.client.email:client@cinema.local}")
    private String clientEmail;

    @Value("${security.auth.client.password:Client123!}")
    private String clientPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        createUserIfMissing(adminEmail, adminPassword, Role.ADMIN);
        createUserIfMissing(clientEmail, clientPassword, Role.CLIENT);
    }

    private void createUserIfMissing(String email, String rawPassword, Role role) {
        String normalizedEmail = email.trim().toLowerCase();
        if (appUserRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        AppUser user = AppUser.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .enabled(true)
                .build();
        appUserRepository.save(user);
    }
}
