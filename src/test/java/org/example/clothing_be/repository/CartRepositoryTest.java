package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Cart;
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
public class CartRepositoryTest extends BaseRepositoryTest{


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // 1. Tạo User trước vì Cart cần User
        savedUser = User.builder()
                .email("testuser@gmail.com")
                .password("password123")
                .status(Status.ACTIVE)
                .build();
        savedUser = entityManager.persist(savedUser);


        Cart cart = new Cart();
        cart.setUser(savedUser);

        entityManager.persist(cart);
        entityManager.flush();
    }

    @Test
    void findByUserId_shouldReturnCart_whenUserHasCart() {
        // Act
        Optional<Cart> foundCart = cartRepository.findByUserId(savedUser.getId());

        // Assert
        Assertions.assertTrue(foundCart.isPresent());
        Assertions.assertEquals(savedUser.getId(), foundCart.get().getUser().getId());
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserHasNoCart() {
        // Arrange
        Long nonExistentUserId = 999L;

        // Act
        Optional<Cart> result = cartRepository.findByUserId(nonExistentUserId);

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

}
