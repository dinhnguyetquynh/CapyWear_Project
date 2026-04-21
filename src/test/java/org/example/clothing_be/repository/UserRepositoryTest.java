package org.example.clothing_be.repository;

import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends BaseRepositoryTest{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;
    @BeforeEach
    void setUp() {
        savedUser = entityManager.persist(
                User.builder()
                        .email("test@gmail.com")
                        .password("123456")
                        .status(Status.ACTIVE)
                        .build()
        );

        entityManager.flush();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        boolean exists = userRepository.existsByEmail("test@gmail.com");
        Assertions.assertEquals(true,exists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        boolean exists = userRepository.existsByEmail("notfound@gmail.com");

        Assertions.assertEquals(false,exists);
    }
    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        Optional<User> result = userRepository.findByEmail("test@gmail.com");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("test@gmail.com", result.get().getEmail());
    }
    @Test
    void findByEmail_shouldReturnEmpty_whenEmailNotExists() {
        Optional<User> result = userRepository.findByEmail("abc@gmail.com");

        Assertions.assertFalse(result.isPresent());
    }
}