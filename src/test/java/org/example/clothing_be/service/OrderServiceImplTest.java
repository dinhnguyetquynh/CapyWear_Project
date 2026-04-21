package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.res.OrderPendingRes;
import org.example.clothing_be.dto.users.request.OrderRequest;
import org.example.clothing_be.dto.users.respone.OrderDetailDTO;
import org.example.clothing_be.dto.users.respone.OrderResponse;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.OrderDetail;
import org.example.clothing_be.entity.Orders;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.OrderStatus;
import org.example.clothing_be.exception.ResourceNotFoundException;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.OrdersRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.serviceImpl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrdersRepository orderRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private void mockUser(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createOrder_shouldCreateOrderAndUpdateInventory() {
        // Arrange
        mockUser("buyer@example.com");
        User user = new User();
        user.setId(1L);
        user.setEmail("buyer@example.com");

        Item item = new Item();
        item.setId(10);
        item.setPrice(20.0);
        item.setInventoryQty(10);
        item.setName("TestItem");
        item.setUrlImg("img");

        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(itemRepository.findById(10)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Orders.class))).thenAnswer(i -> {
            Orders o = i.getArgument(0);
            o.setId(99);
            return o;
        });

        OrderRequest req = new OrderRequest();
        OrderRequest.ItemRequest ir = new OrderRequest.ItemRequest();
        ir.setItemId(10);
        ir.setQuantity(3);
        req.setItems(List.of(ir));

        // Act
        OrderResponse res = orderService.createOrder(req);

        // Assert
        assertNotNull(res);
        assertEquals(3 * 20.0, res.getTotalOrder());
        assertEquals("PENDING", res.getStatus());
        assertEquals(1, res.getDetails().size());
        // Inventory decreased and saved
        assertEquals(7, item.getInventoryQty());
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(orderRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void createOrder_shouldThrowWhenItemMissing() {
        // Arrange
        mockUser("buyer2@example.com");
        User user = new User();
        user.setId(2L);
        user.setEmail("buyer2@example.com");

        when(userRepository.findByEmail("buyer2@example.com")).thenReturn(Optional.of(user));
        when(itemRepository.findById(42)).thenReturn(Optional.empty());

        OrderRequest req = new OrderRequest();
        OrderRequest.ItemRequest ir = new OrderRequest.ItemRequest();
        ir.setItemId(42);
        ir.setQuantity(1);
        req.setItems(List.of(ir));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.createOrder(req));
        assertTrue(ex.getMessage().contains("Item not found"));
    }

    @Test
    void getOrdersHistory_shouldReturnMappedResponses() {
        // Arrange
        mockUser("hist@example.com");
        User user = new User();
        user.setId(5L);
        user.setEmail("hist@example.com");

        Orders order = new Orders();
        order.setId(100);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.COMPLETE);
        order.setTotalOrder(200.0);

        Item item = new Item();
        item.setId(7);
        item.setName("HistoryItem");
        item.setUrlImg("u");

        OrderDetail od = new OrderDetail();
        od.setId(55);
        od.setItem(item);
        od.setQuantity(2);
        od.setPurchasedPrice(100.0);
        od.setTotalItem(200.0);
        od.setOrder(order);

        order.setOrderDetails(new ArrayList<>());
        order.getOrderDetails().add(od);

        when(userRepository.findByEmail("hist@example.com")).thenReturn(Optional.of(user));
        when(orderRepository.getAllByUser_Id(5L)).thenReturn(List.of(order));

        // Act
        List<OrderResponse> responses = orderService.getOrdersHistory();

        // Assert
        assertEquals(1, responses.size());
        OrderResponse r = responses.get(0);
        assertEquals(200.0, r.getTotalOrder());
        assertEquals("COMPLETE", r.getStatus());
        assertEquals(1, r.getDetails().size());
        OrderDetailDTO dto = r.getDetails().get(0);
        assertEquals("HistoryItem", dto.getItemName());
    }

    @Test
    void getOrdersHistory_shouldThrowWhenUserMissing() {
        // Arrange
        mockUser("nouser@example.com");
        when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrdersHistory());
    }

    @Test
    void getOrdersPending_shouldReturnPendingOrdersMapped() {
        // Arrange
        Orders pending = new Orders();
        pending.setId(200);
        User user = new User();
        user.setEmail("pend@example.com");
        pending.setUser(user);
        pending.setStatus(OrderStatus.PENDING);
        pending.setTotalOrder(50.0);
        pending.setOrderDetails(new ArrayList<>());

        Item it = new Item();
        it.setName("P");
        it.setUrlImg("u2");

        OrderDetail od = new OrderDetail();
        od.setItem(it);
        od.setQuantity(1);
        od.setPurchasedPrice(50.0);
        od.setTotalItem(50.0);
        od.setOrder(pending);
        pending.getOrderDetails().add(od);

        when(orderRepository.findAllByStatus(OrderStatus.PENDING)).thenReturn(List.of(pending));

        // Act
        List<OrderPendingRes> list = orderService.getOrdersPending();

        // Assert
        assertEquals(1, list.size());
        OrderPendingRes res = list.get(0);
        assertEquals(200, res.getOrderId());
        assertEquals("pend@example.com", res.getUserEmail());
        assertEquals(50.0, res.getTotalOrder());
        assertEquals("PENDING", res.getStatus());
        assertEquals(1, res.getDetails().size());
    }

    @Test
    void changeStatusOrder_shouldMarkCompleteAndReturnDTO() {
        // Arrange
        Orders o = new Orders();
        o.setId(300);
        o.setStatus(OrderStatus.PENDING);
        o.setUser(new User());
        o.setOrderDetails(new ArrayList<>());
        o.setTotalOrder(10.0);

        when(orderRepository.findById(300)).thenReturn(Optional.of(o));
        when(orderRepository.save(any(Orders.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        OrderPendingRes res = orderService.changeStatusOrder(300);

        // Assert
        assertEquals("COMPLETE", res.getStatus());
        verify(orderRepository).save(any(Orders.class));
    }

    @Test
    void changeStatusOrder_shouldThrowWhenNotFound() {
        // Arrange
        when(orderRepository.findById(404)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.changeStatusOrder(404));
    }
}
