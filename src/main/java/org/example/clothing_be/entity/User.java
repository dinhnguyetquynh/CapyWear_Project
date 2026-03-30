package org.example.clothing_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.clothing_be.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private LocalDate created_at;
    private Status status;
    private String imgUrl;
    @Column(name = "otp_hash")
    private String otpHash;
    @Column(name = "otp_expired_at")
    private LocalDateTime optExpiredAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPermission> userPermissions = new ArrayList<>();
}
