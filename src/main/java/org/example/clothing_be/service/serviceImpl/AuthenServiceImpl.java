package org.example.clothing_be.service.serviceImpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clothing_be.config.JwtUtils;
import org.example.clothing_be.dto.auth.AuthResponse;
import org.example.clothing_be.dto.users.request.AccountCreateReq;
import org.example.clothing_be.dto.users.request.LoginReq;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.entity.*;
import org.example.clothing_be.enums.Status;


import org.example.clothing_be.exception.EmailAlreadyExistsException;
import org.example.clothing_be.exception.InvalidEmailException;
import org.example.clothing_be.exception.ResourceNotFoundException;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
    @Value("${jwt.expiration}")
    private int expiresIn;

    @Value("${google.client.id}") // Lưu clientId của bạn trong application.properties
    private String googleClientId;

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
        Set<String> authorities = new HashSet<>();
        for (UserRole userRole : user.getUserRoles()) {
            Role role = userRole.getRole();
            for (RolePermission rolePermission : role.getRolePermissions()) {
                Permission perm = rolePermission.getPermission();
                if (perm.getAction() != null && perm.getResource() != null) {
                    authorities.add(perm.getAction() + ":" + perm.getResource());
                }
            }
        }

        if (user.getUserPermissions() != null) {
            for (UserPermission userPermission : user.getUserPermissions()) {
                Permission perm = userPermission.getPermission();
                if (perm.getAction() != null && perm.getResource() != null) {
                    authorities.add(perm.getAction() + ":" + perm.getResource());
                }
            }
        }

        List<String> authorityList = new ArrayList<>(authorities);

        String accessToken = jwtUtils.generateToken(user.getEmail(),authorityList,roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken,expiresIn);
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

            Set<String> authorities = new HashSet<>();
            for (UserRole userRole : user.getUserRoles()) {
                Role role = userRole.getRole();
                for (RolePermission rolePermission : role.getRolePermissions()) {
                    Permission perm = rolePermission.getPermission();
                    if (perm.getAction() != null && perm.getResource() != null) {
                        authorities.add(perm.getAction() + ":" + perm.getResource());
                    }
                }
            }

            if (user.getUserPermissions() != null) {
                for (UserPermission userPermission : user.getUserPermissions()) {
                    Permission perm = userPermission.getPermission();
                    if (perm.getAction() != null && perm.getResource() != null) {
                        authorities.add(perm.getAction() + ":" + perm.getResource());
                    }
                }
            }

            List<String> authorityList = new ArrayList<>(authorities);
            List<String> roles = new ArrayList<>();
            for (UserRole userRole : user.getUserRoles()) {
                roles.add(userRole.getRole().getRoleName());
            }

            String newAccessToken = jwtUtils.generateToken(user.getEmail(), authorityList,roles);
            return new AuthResponse(newAccessToken, oldRefreshToken,expiresIn);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
        }
    }

    @Override
    @Transactional
    public AuthResponse login(LoginReq req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InvalidEmailException());

        boolean isPasswordMatch = passwordEncoder.matches(req.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            throw new InvalidEmailException();
        }

        Set<String> authorities = new HashSet<>();
        for (UserRole userRole : user.getUserRoles()) {
            Role role = userRole.getRole();
            for (RolePermission rolePermission : role.getRolePermissions()) {
                Permission perm = rolePermission.getPermission();
                if (perm.getAction() != null && perm.getResource() != null) {
                    authorities.add(perm.getAction() + ":" + perm.getResource());
                }
            }
        }

        if (user.getUserPermissions() != null) {
            for (UserPermission userPermission : user.getUserPermissions()) {
                Permission perm = userPermission.getPermission();
                if (perm.getAction() != null && perm.getResource() != null) {
                    authorities.add(perm.getAction() + ":" + perm.getResource());
                }
            }
        }

        List<String> authorityList = new ArrayList<>(authorities);
        List<String> roles = new ArrayList<>();
        for (UserRole userRole : user.getUserRoles()) {
            roles.add(userRole.getRole().getRoleName());
        }

        String accessToken = jwtUtils.generateToken(user.getEmail(), authorityList,roles);
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken,expiresIn);
    }

    @Override
    public AuthResponse socialLogin(Map<String, String> request) throws Exception{
        String idTokenString = request.get("idToken");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setPassword(null);
                        newUser.setCreated_at(LocalDate.now());
                        newUser.setStatus(Status.ACTIVE);
                        User saveUser = userRepository.save(newUser);

                        UserRole userRole = new UserRole();
                        Role role = roleRepository.findByRoleName("USER")
                                .orElseThrow(()-> new ResourceNotFoundException("Khong tim thay role USER"));
                        userRole.setRole(role);
                        userRole.setUser(saveUser);
                        List<UserRole> userRoleList = new ArrayList<>();
                        userRoleList.add(userRole);

                        saveUser.setUserRoles(userRoleList);

                        return userRepository.save(saveUser);
                    });


            Set<String> authorities = new HashSet<>();
            for (UserRole userRole : user.getUserRoles()) {
                Role role = userRole.getRole();
                for (RolePermission rolePermission : role.getRolePermissions()) {
                    Permission perm = rolePermission.getPermission();
                    if (perm.getAction() != null && perm.getResource() != null) {
                        authorities.add(perm.getAction() + ":" + perm.getResource());
                    }
                }
            }

            if (user.getUserPermissions() != null) {
                for (UserPermission userPermission : user.getUserPermissions()) {
                    Permission perm = userPermission.getPermission();
                    if (perm.getAction() != null && perm.getResource() != null) {
                        authorities.add(perm.getAction() + ":" + perm.getResource());
                    }
                }
            }

            List<String> authorityList = new ArrayList<>(authorities);
            List<String> roles = new ArrayList<>();
            for (UserRole userRole : user.getUserRoles()) {
                roles.add(userRole.getRole().getRoleName());
            }
            String accessToken = jwtUtils.generateToken(user.getEmail(), authorityList,roles);
            String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
            return new AuthResponse(accessToken, refreshToken, expiresIn);

        } else {
            throw new RuntimeException("Invalid Google Token");
        }
    }

    public UserRes toDTO(User u){
        UserRes userRes = new UserRes();
        userRes.setId(u.getId());
        userRes.setEmail(u.getEmail());
        userRes.setCreated_at(u.getCreated_at());
        userRes.setStatus(u.getStatus().name());
        userRes.setImgUrl(u.getImgUrl());
        return userRes;
    }
}
