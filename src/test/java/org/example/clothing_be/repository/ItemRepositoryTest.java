package org.example.clothing_be.repository;

import org.example.clothing_be.entity.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest extends BaseRepositoryTest{
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Tạo các item để test tìm kiếm và tồn kho
        entityManager.persist(createItem("Ao somi", 100, false));
        entityManager.persist(createItem("Ao tay dai", 5, false)); // Low stock
        entityManager.persist(createItem("Quan dai ", 2, false)); // Low stock
        entityManager.persist(createItem("Non", 50, true)); // Deleted
        entityManager.persist(createItem("Ao khoac", 20, false));

        // Tạo thêm nhiều item để test Top 10
        for (int i = 1; i <= 15; i++) {
            entityManager.persist(createItem("Ao trang " + i, 50, false));
        }

        entityManager.flush();
    }

    private Item createItem(String name, int qty, boolean deleted) {
        Item item = new Item();
        item.setName(name);
        item.setInventoryQty(qty);
        item.setDeleted(deleted);
        item.setPrice(1000.0); // Giả sử có field price
        return item;
    }

    @Test
    void existsByName_shouldReturnTrue_whenNameExists() {
        boolean exists = itemRepository.existsByName("Non");
        Assertions.assertTrue(exists);
    }

    @Test
    void findAllByDeletedFalse_shouldReturnOnlyActiveItems() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> activeItems = itemRepository.findAllByDeletedFalse(pageable);

        Assertions.assertEquals(19, activeItems.getTotalElements());
        Assertions.assertTrue(activeItems.getContent().stream().noneMatch(Item::isDeleted));
    }

    @Test
    void findTop10ByNameStartingWithIgnoreCase_shouldReturnLimitAndIgnoreOrder() {
        List<Item> results = itemRepository.findTop10ByNameStartingWithIgnoreCase("ao");

        Assertions.assertEquals(10, results.size());
        Assertions.assertTrue(results.get(0).getName().toLowerCase().startsWith("ao"));
    }

    @Test
    void findLowStockItems_shouldReturnCorrectItems() {
        List<Item> lowStock = itemRepository.findLowStockItems(5);

        Assertions.assertEquals(2, lowStock.size());
        Assertions.assertTrue(lowStock.stream().allMatch(i -> i.getInventoryQty() <= 5));
    }

    @Test
    void findTop10ByNameContainingIgnoreCase_shouldReturnMatchingItems() {
        List<Item> results = itemRepository.findTop10ByNameContainingIgnoreCase("dai");

        Assertions.assertEquals(2, results.size());
        Assertions.assertTrue(results.get(0).getName().toLowerCase().contains("dai"));
    }
}
