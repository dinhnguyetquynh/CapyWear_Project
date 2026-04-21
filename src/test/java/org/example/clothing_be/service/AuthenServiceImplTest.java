package org.example.clothing_be.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.auth.AuthResponse;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.request.LoginReq;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.entity.Role;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.entity.UserRole;
import org.example.clothing_be.enums.Status;
import org.example.clothing_be.exception.EmailAlreadyExistsException;
import org.example.clothing_be.exception.InvalidEmailException;
import org.example.clothing_be.repository.RoleRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.serviceImpl.AuthenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenServiceImplTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenServiceImpl authService;

    @BeforeEach
    void setup() {
        // ensure deterministic fromEmail and googleClientId values if needed
        // using reflection to set @Value fields is possible but not required for these tests
    }

    @Test
    void generateOtp_shouldReturnSixDigitNumeric() {
        String otp = authService.generateOtp();
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.chars().allMatch(Character::isDigit));
    }

    @Test
    void sendOtpEmail_shouldCallMailSender() {
        String to = "user@example.com";
        String otp = "123456";

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        authService.sendOtpEmail(to, otp);

        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{to}, sent.getTo());
        assertTrue(sent.getText().contains(otp));
    }

    @Test
    void creatAccount_whenEmailExists_shouldThrow() {
        AccountCreateReq req = new AccountCreateReq();
        req.setEmail("exists@example.com");
        req.setPassword("pass");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.creatAccount(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void creatAccount_success_shouldReturnUserRes() {
        AccountCreateReq req = new AccountCreateReq();
        req.setEmail("new@example.com");
        req.setPassword("pass");

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("USER");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenAnswer(i -> "encoded-" + i.getArgument(0));

        User saved = new User();
        saved.setId(10L);
        saved.setEmail(req.getEmail());
        saved.setCreated_at(LocalDate.now());
        saved.setStatus(Status.INACTIVE);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserRes res = authService.creatAccount(req);

        assertNotNull(res);
        assertEquals(10L, res.getId());
        assertEquals("new@example.com", res.getEmail());
        assertEquals("INACTIVE", res.getStatus());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void verifyOtp_success_shouldActivateAndReturnTokens() {
        Long userId = 5L;
        String otp = "654321";

        User user = new User();
        user.setId(userId);
        user.setEmail("u@example.com");
        user.setOtpHash("encoded-otp");
        user.setOptExpiredAt(LocalDateTime.now().plusMinutes(3));
        user.setStatus(Status.INACTIVE);

        // minimal role/permission setup
        Role role = new Role();
        role.setRoleName("USER");
        UserRole ur = new UserRole();
        ur.setRole(role);
        ur.setUser(user);
        user.setUserRoles(List.of(ur));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(otp), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString(), anyList(), anyList())).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        AuthResponse resp = authService.verifyOtp(otp, userId);

        assertNotNull(resp);
        assertEquals("access-token", resp.getAccessToken());
        assertEquals("refresh-token", resp.getRefreshToken());
        assertEquals(Status.ACTIVE, user.getStatus());
        assertNull(user.getOtpHash());
        verify(userRepository).save(user);
    }

    @Test
    void login_invalidPassword_shouldThrow() {
        LoginReq req = new LoginReq();
        req.setEmail("user@example.com");
        req.setPassword("wrong");

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword("encoded-pass");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidEmailException.class, () -> authService.login(req));
    }

    @Test
    void refreshToken_whenExpired_shouldThrowRuntimeException() {
        String oldRefresh = "old-refresh";

        doThrow(new ExpiredJwtException(null, null, "expired")).when(jwtUtils).validateToken(oldRefresh);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refreshToken(oldRefresh));
        assertTrue(ex.getMessage().contains("Phiên đăng nhập hết hạn"));
    }
}
