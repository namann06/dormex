package com.dormex.config;

import com.dormex.entity.User;
import com.dormex.entity.enums.AuthProvider;
import com.dormex.entity.enums.Role;
import com.dormex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Override
    public void run(String... args) {
        if (adminPassword == null || adminPassword.isBlank() || "changeme-on-first-login".equals(adminPassword)) {
            log.warn("SECURITY WARNING: Admin password is not set or using default. Set ADMIN_PASSWORD environment variable!");
        }

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                .name(adminName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .build();

            userRepository.save(admin);
            log.info("Default admin created: {}", adminEmail);
        }

        // Fix: downgrade any Google OAuth users that were incorrectly set as ADMIN
        userRepository.findAll().stream()
            .filter(u -> u.getAuthProvider() == AuthProvider.GOOGLE
                      && u.getRole() == Role.ADMIN
                      && !u.getEmail().equals(adminEmail))
            .forEach(u -> {
                u.setRole(Role.STUDENT);
                userRepository.save(u);
                log.info("Downgraded Google OAuth user {} to STUDENT role", u.getEmail());
            });
    }
}
