package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Cart;
import org.example.clothing_be.entity.CartDetail;
import org.example.clothing_be.entity.Item;
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

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartDetailRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;
    private Cart savedCart;
    private Item savedItem;

    @BeforeEach
    void setUp() {
        // 1. Tạo User
        savedUser = entityManager.persist(User.builder()
                .email("buyer@gmail.com")
                .password("123")
                .status(Status.ACTIVE)
                .build());

        // 2. Tạo Cart cho User
        Cart cart = new Cart();
        cart.setUser(savedUser);
        savedCart = entityManager.persist(cart);

        // 3. Tạo Item (Sản phẩm)
        Item item = new Item();
        item.setName("Laptop Gaming");
        item.setPrice(2000.0);
        savedItem = entityManager.persist(item);

        // 4. Tạo CartDetail (Sản phẩm trong giỏ hàng)
        CartDetail cd = new CartDetail();
        cd.setCart(savedCart);
        cd.setItem(savedItem);
        cd.setQuantity(2);
        entityManager.persist(cd);

        entityManager.flush();
    }

    @Test
    void existsByItemId_shouldReturnTrue_whenItemExistsInAnyCart() {
        boolean exists = cartDetailRepository.existsByItemId(savedItem.getId());
        Assertions.assertTrue(exists);
    }

    @Test
    void findByCartIdAndItemId_shouldReturnDetail_whenMatch() {
        Optional<CartDetail> result = cartDetailRepository.findByCartIdAndItemId(
                savedCart.getId(), savedItem.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(2, result.get().getQuantity());
    }

    @Test
    void findByCartId_shouldReturnList() {
        List<CartDetail> details = cartDetailRepository.findByCartId(savedCart.getId());
        Assertions.assertEquals(1, details.size());
        Assertions.assertEquals("Laptop Gaming", details.get(0).getItem().getName());
    }

    @Test
    void findAllByUserIdWithItem_shouldFetchItemDetails() {
        // Test hàm @Query có JOIN FETCH
        List<CartDetail> results = cartDetailRepository.findAllByUserIdWithItem(savedUser.getId());

        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        // Kiểm tra xem Item có được load lên không (tránh LazyInitializationException)
        Assertions.assertNotNull(results.get(0).getItem().getName());
        Assertions.assertEquals(savedUser.getId(), results.get(0).getCart().getUser().getId());
    }
}
