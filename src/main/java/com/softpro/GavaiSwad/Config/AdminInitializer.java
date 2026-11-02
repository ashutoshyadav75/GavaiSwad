package com.softpro.GavaiSwad.Config;

import com.softpro.GavaiSwad.Entity.User;
import com.softpro.GavaiSwad.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String normalizedEmail =
                    adminEmail.trim().toLowerCase();

            if (userRepository.existsByEmail(normalizedEmail)) {
                System.out.println(
                        "Admin email already exists. No new account created."
                );
                return;
            }

            User admin = new User();
            admin.setFullName("Gavai Swad Admin");
            admin.setEmail(normalizedEmail);
            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );
            admin.setRole("ADMIN");
            admin.setAccountStatus("APPROVED");

            userRepository.save(admin);

            System.out.println(
                    "Initial admin account created successfully."
            );
        };
    }
}