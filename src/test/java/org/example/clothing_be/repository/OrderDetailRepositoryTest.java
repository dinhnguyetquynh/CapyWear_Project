package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.OrderDetail;
import org.example.clothing_be.entity.Orders;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.OrderStatus;
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

import java.time.LocalDate;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderDetailRepositoryTest extends BaseRepositoryTest{
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Item savedItem;
    @BeforeEach
    void setUp() {
        // 1. Tạo User
        User user = User.builder()
                .email("customer@gmail.com")
                .password("123")
                .status(Status.ACTIVE)
                .build();
        user = entityManager.persist(user);

        // 2. Tạo Order (Đơn hàng tổng)
        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order = entityManager.persist(order);

        // 3. Tạo Item (Sản phẩm)
        savedItem = new Item();
        savedItem.setName("Bàn phím cơ");
        savedItem.setPrice(1500.0);
        savedItem = entityManager.persist(savedItem);

        // 4. Tạo OrderDetail (Chi tiết sản phẩm trong đơn hàng đó)
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setItem(savedItem);
        orderDetail.setQuantity(1);
        orderDetail.setPurchasedPrice(1500.0);
        entityManager.persist(orderDetail);

        entityManager.flush();
    }

    @Test
    void existsByItemId_shouldReturnTrue_whenItemIsAlreadyOrdered() {
        // Act
        boolean exists = orderDetailRepository.existsByItemId(savedItem.getId());

        // Assert
        Assertions.assertTrue(exists);
    }

    @Test
    void existsByItemId_shouldReturnFalse_whenItemHasNeverBeenOrdered() {
        // Arrange: Tạo một item mới nhưng không đưa vào bất kỳ OrderDetail nào
        Item newItem = new Item();
        newItem.setName("Ao hoodie");
        newItem.setPrice(500.0);
        newItem = entityManager.persist(newItem);
        entityManager.flush();

        // Act
        boolean exists = orderDetailRepository.existsByItemId(newItem.getId());

        // Assert
        Assertions.assertFalse(exists);
    }
}
