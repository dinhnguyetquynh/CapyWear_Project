package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;
import org.example.clothing_be.entity.Cart;
import org.example.clothing_be.entity.CartDetail;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.exception.*;
import org.example.clothing_be.repository.CartDetailRepository;
import org.example.clothing_be.repository.CartRepository;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.serviceImpl.CartDetailImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartDetailImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartDetailImpl cartService;

    private void mockUser(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addItem_shouldCreateNewCartDetail_whenItemNotExistInCart() {
        // Arrange
        mockUser("test@gmail.com");

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10);
        item.setPrice(100);
        item.setDeleted(false);

        Cart cart = new Cart();
        cart.setId(5);
        cart.setUser(user);

        CartDetailReq req = new CartDetailReq();
        req.setItemId(10);
        req.setQuantity(2);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(10))
                .thenReturn(Optional.of(item));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartDetailRepository.findByCartIdAndItemId(5, 10))
                .thenReturn(Optional.empty());

        when(cartDetailRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(cartDetailRepository.findByCartId(5))
                .thenReturn(List.of());

        // Act
        CartDetailRes result = cartService.addItem(req);

        // Assert
        assertEquals(2, result.getQuantity());
        assertEquals(200, result.getTotalItem());

        verify(cartDetailRepository).save(any());
        verify(cartRepository).save(any());
    }
    @Test
    void addItem_shouldUpdateQuantity_whenItemExistsInCart() {
        // Arrange
        mockUser("test@gmail.com");

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10);
        item.setPrice(100);
        item.setDeleted(false);

        Cart cart = new Cart();
        cart.setId(5);

        CartDetail existing = new CartDetail();
        existing.setCart(cart);
        existing.setItem(item);
        existing.setQuantity(1);
        existing.setPurchasePrice(100);
        existing.setTotalItem(100);

        CartDetailReq req = new CartDetailReq();
        req.setItemId(10);
        req.setQuantity(2);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(10))
                .thenReturn(Optional.of(item));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartDetailRepository.findByCartIdAndItemId(5, 10))
                .thenReturn(Optional.of(existing));

        when(cartDetailRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(cartDetailRepository.findByCartId(5))
                .thenReturn(List.of(existing));

        // Act
        CartDetailRes result = cartService.addItem(req);

        // Assert
        assertEquals(3, result.getQuantity()); // 1 + 2
        assertEquals(300, result.getTotalItem());
    }
    @Test
    void addItem_shouldThrowUserNotFoundException_whenUserNotFound() {
        // Arrange
        mockUser("test@gmail.com");

        CartDetailReq req = new CartDetailReq();
        req.setItemId(10);
        req.setQuantity(2);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> cartService.addItem(req));
    }
    @Test
    void addItem_shouldThrowItemNotFoundException_whenItemNotFound() {
        // Arrange
        mockUser("test@gmail.com");

        User user = new User();
        user.setId(1L);

        CartDetailReq req = new CartDetailReq();
        req.setItemId(10);
        req.setQuantity(2);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(10))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class,
                () -> cartService.addItem(req));
    }
    @Test
    void addItem_shouldThrowProductNotAvailableException_whenItemDeleted() {
        // Arrange
        mockUser("test@gmail.com");

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10);
        item.setDeleted(true);

        CartDetailReq req = new CartDetailReq();
        req.setItemId(10);
        req.setQuantity(2);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(10))
                .thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(ProductNotAvailableException.class,
                () -> cartService.addItem(req));
    }

    @Test
    void getAllByUser_shouldReturnListOfCartDetails() {
        // Arrange
        mockUser("user@example.com");
        User user = new User();
        user.setId(2L);

        Item item = new Item();
        item.setId(11);
        item.setPrice(50);

        CartDetail cd = new CartDetail();
        cd.setId(100);
        cd.setItem(item);
        cd.setQuantity(3);
        cd.setTotalItem(150);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(cartDetailRepository.findAllByUserIdWithItem(2L))
                .thenReturn(List.of(cd));

        // Act
        var result = cartService.getAllByUser(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        CartDetailRes res = result.get(0);
        assertEquals(3, res.getQuantity());
        assertEquals(150, res.getTotalItem());
    }

    @Test
    void updateCartDetail_shouldUpdateQuantityAndRecalculateCart() {
        // Arrange
        Item item = new Item();
        item.setId(20);
        item.setPrice(40);

        Cart cart = new Cart();
        cart.setId(7);
        cart.setTotalCart(0);

        CartDetail cd = new CartDetail();
        cd.setId(55);
        cd.setCart(cart);
        cd.setItem(item);
        cd.setQuantity(1);
        cd.setPurchasePrice(40);
        cd.setTotalItem(40);

        when(cartDetailRepository.findById(55))
                .thenReturn(Optional.of(cd));
        // save returns the same object after modification
        when(cartDetailRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        // recalculateCartTotal will query details by cart id
        when(cartDetailRepository.findByCartId(7))
                .thenReturn(List.of(cd));
        when(cartRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        CartDetailRes result = cartService.updateCartDetail(55, 5);

        // Assert
        assertEquals(5, result.getQuantity());
        assertEquals(5 * 40, result.getTotalItem());
        verify(cartDetailRepository).save(any());
        verify(cartRepository).save(any());
    }

    @Test
    void updateCartDetail_shouldThrowWhenNotFound() {
        // Arrange
        when(cartDetailRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartDetailNotFound.class, () -> cartService.updateCartDetail(999, 2));
    }

    @Test
    void deleteCartDetail_shouldRemoveAndUpdateCartTotal() {
        // Arrange
        Item item = new Item();
        item.setId(30);
        item.setPrice(25);

        Cart cart = new Cart();
        cart.setId(12);
        cart.setTotalCart(100);

        CartDetail cd = new CartDetail();
        cd.setId(77);
        cd.setCart(cart);
        cd.setTotalItem(25);

        when(cartDetailRepository.findById(77))
                .thenReturn(Optional.of(cd));
        when(cartRepository.findById(12))
                .thenReturn(Optional.of(cart));
        when(cartRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        cartService.deleteCartDetail(77);

        // Assert
        verify(cartDetailRepository).delete(cd);
        verify(cartRepository).save(cart);
        // New total should be 75 (100 - 25)
        assertEquals(75.0, cart.getTotalCart());
    }

    @Test
    void deleteCartDetail_shouldThrowWhenCartDetailMissing() {
        // Arrange
        when(cartDetailRepository.findById(888))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartDetailNotFound.class, () -> cartService.deleteCartDetail(888));
    }

    @Test
    void deleteCartDetail_shouldThrowWhenCartMissing() {
        // Arrange
        Cart cart = new Cart();
        cart.setId(44);

        CartDetail cd = new CartDetail();
        cd.setId(200);
        cd.setCart(cart);
        cd.setTotalItem(10);

        when(cartDetailRepository.findById(200))
                .thenReturn(Optional.of(cd));
        when(cartRepository.findById(44))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartDetail(200));
    }
}
