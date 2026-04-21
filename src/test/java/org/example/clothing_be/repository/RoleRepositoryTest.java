package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Tạo dữ liệu mẫu các vai trò trong hệ thống
        Role adminRole = new Role();
        adminRole.setRoleName("ROLE_ADMIN");
        entityManager.persist(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("ROLE_USER");
        entityManager.persist(userRole);

        entityManager.flush();
    }

    @Test
    void findByRoleName_shouldReturnRole_whenRoleNameExists() {
        // Act
        Optional<Role> foundRole = roleRepository.findByRoleName("ROLE_ADMIN");

        // Assert
        Assertions.assertTrue(foundRole.isPresent());
        Assertions.assertEquals("ROLE_ADMIN", foundRole.get().getRoleName());
    }

    @Test
    void findByRoleName_shouldReturnEmpty_whenRoleNameDoesNotExist() {
        // Act
        Optional<Role> result = roleRepository.findByRoleName("ROLE_MODERATOR");

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void findByRoleName_shouldBeCaseSensitive_dependingOnDbConfig() {
        Optional<Role> result = roleRepository.findByRoleName("role_admin");

        Assertions.assertFalse(result.isPresent());
    }
}
