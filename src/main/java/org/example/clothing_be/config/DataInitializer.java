package org.example.clothing_be.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.entity.UserRole;
import org.example.clothing_be.enums.Role;
import org.example.clothing_be.enums.Status;
import org.example.clothing_be.exception.ResourceNotFoundException;
import org.example.clothing_be.repository.RoleRepository;
import org.example.clothing_be.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin202@gmail.com";

        // 1. Kiểm tra xem admin đã tồn tại chưa
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Admin account not found. Creating default admin...");

            // 2. Tạo tài khoản admin mới
            User admin = User.builder()
                    .email("admin202@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .created_at(LocalDate.now())
                    .status(Status.ACTIVE)
                    .build();
            User savedAmin = userRepository.save(admin);

            org.example.clothing_be.entity.Role role = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(()-> new ResourceNotFoundException("Not found role"));


            UserRole userRole = new UserRole();
            userRole.setUser(savedAmin);
            userRole.setRole(role);

            List<UserRole> userRoles = new ArrayList<>();
            userRoles.add(userRole);
            savedAmin.setUserRoles(userRoles);

            userRepository.save(savedAmin);
            log.info("Default admin account created successfully!");
        } else {
            log.info("Admin account already exists. Skipping initialization.");
        }
    }
}
