package org.example.clothing_be.service.serviceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
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
import org.example.clothing_be.exception.UserNotFoundException;
import org.example.clothing_be.repository.RoleRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.AuthenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenServiceImpl implements AuthenService {
    private final JavaMailSender mailSender;
    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }
        return otp.toString();
    }

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Mã xác thực OTP của bạn");
        message.setText("Chào bạn,\n\nMã OTP để hoàn tất đăng ký tài khoản của bạn là: " + otpCode +
                "\nMã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ cho bất kỳ ai.");

        mailSender.send(message);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(String inputOtp, Long userId) {
        User user =userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("Not found user"));

        if (user.getOptExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The OTP code has expired.");
        }

        boolean isMatches = passwordEncoder.matches(inputOtp,user.getOtpHash());
        if (!isMatches) {
            throw new RuntimeException("OTP is incorrect");
        }
        user.setOtpHash(null);
        user.setOptExpiredAt(null);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .toList();
        String accessToken = jwtUtils.generateToken(user.getEmail(), roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public UserRes creatAccount(AccountCreateReq req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException(req.getEmail());
        }
        String otp = generateOtp();
        sendOtpEmail(req.getEmail(), otp);

        Role userRole = roleRepository.findByRoleName(org.example.clothing_be.enums.Role.USER.name())
                .orElseThrow(() -> new RuntimeException("NOT FIND ROLE"));

        User newUser = new User();
        newUser.setEmail(req.getEmail());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        newUser.setCreated_at(LocalDate.now());
        newUser.setStatus(Status.INACTIVE);
        newUser.setOtpHash(passwordEncoder.encode(otp));
        newUser.setOptExpiredAt(LocalDateTime.now().plusMinutes(5));

        UserRole mapping = new UserRole(newUser, userRole);
        newUser.getUserRoles().add(mapping);

        User savedUser = userRepository.save(newUser);


        UserRes userRes = toDTO(savedUser);
        return userRes;
    }

    @Override
    public AuthResponse refreshToken(String oldRefreshToken) {
        try {
            jwtUtils.validateToken(oldRefreshToken);

            String email = jwtUtils.extractUsername(oldRefreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException());

            List<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getRoleName())
                    .toList();

            String newAccessToken = jwtUtils.generateToken(email, roles);

            return new AuthResponse(newAccessToken, oldRefreshToken);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
        }
    }

    @Override
    public AuthResponse login(LoginReq req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidEmailException());

        boolean isPasswordMatch = passwordEncoder.matches(req.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new InvalidEmailException();
        }

        List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .toList();
        String accessToken = jwtUtils.generateToken(user.getEmail(), roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken);
    }

    public UserRes toDTO(User u){
        UserRes userRes = new UserRes();
        userRes.setId(u.getId());
        userRes.setEmail(u.getEmail());
        userRes.setCreated_at(u.getCreated_at());
        userRes.setStatus(u.getStatus());
        userRes.setImgUrl(u.getImgUrl());
        return userRes;
    }
}
