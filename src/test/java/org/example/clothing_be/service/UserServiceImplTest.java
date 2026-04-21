package org.example.clothing_be.service;

import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.enums.Status;
import org.example.clothing_be.exception.UserNotFoundException;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private void mockUserInSecurityContext(String email) {
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
    void getProfileUser_shouldReturnMappedUser() throws Exception {
        // Arrange
        mockUserInSecurityContext("user@example.com");

        User user = new User();
        user.setId(10L);
        user.setEmail("user@example.com");
        user.setImgUrl("http://img");
        user.setStatus(Status.ACTIVE);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        UserRes res = userService.getProfileUser();

        // Assert
        assertNotNull(res);
        assertEquals(10L, res.getId());
        assertEquals("user@example.com", res.getEmail());
        assertEquals("http://img", res.getImgUrl());

        assertEquals(Status.ACTIVE.name(), res.getStatus());

    }

    @Test
    void getProfileUser_shouldThrowWhenUserNotFound() {
        // Arrange
        mockUserInSecurityContext("nouser@example.com");
        when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getProfileUser());
    }
}
