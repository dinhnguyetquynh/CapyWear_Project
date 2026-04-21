package org.example.clothing_be.repository;

import org.example.clothing_be.dto.admin.res.OrderPendingRes;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private TestEntityManager entityManager;
    private User savedUser;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = entityManager.persist(
                User.builder()
                        .email("user1@gmail.com")
                        .password("123")
                        .status(Status.ACTIVE)
                        .build()
        );

        user2 = entityManager.persist(
                User.builder()
                        .email("user2@gmail.com")
                        .password("123")
                        .status(Status.ACTIVE)
                        .build()
        );

        // Orders của user1
        entityManager.persist(createOrder(user1, OrderStatus.PENDING, 100));
        entityManager.persist(createOrder(user1, OrderStatus.COMPLETE, 200));

        // Orders của user2
        entityManager.persist(createOrder(user2, OrderStatus.PENDING, 300));

        entityManager.flush();
    }

    private Orders createOrder(User user, OrderStatus status, double total) {
        Orders o = new Orders();
        o.setUser(user);
        o.setStatus(status);
        o.setTotalOrder(total);
        o.setOrderDate(LocalDate.now());
        return o;
    }

    @Test
    void getAllByUserId_shouldReturnCorrectOrders() {
        List<Orders> result = ordersRepository.getAllByUser_Id(user1.getId());
        Assertions.assertEquals(2,result.size());
    }

    @Test
    void findAllByStatus_Test(){
        List<Orders> ordersList = ordersRepository.findAllByStatus(OrderStatus.PENDING);
        Assertions.assertEquals(2,ordersList.size());
    }

}
