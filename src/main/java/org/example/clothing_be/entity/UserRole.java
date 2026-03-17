package org.example.clothing_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {
    @EmbeddedId
    private UserRoleId id = new UserRoleId();
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Map thuộc tính userId trong class UserRoleId
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // Map thuộc tính roleId trong class UserRoleId
    @JoinColumn(name = "role_id")
    private Role role;

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(user.getId(), role.getId());
    }
}
