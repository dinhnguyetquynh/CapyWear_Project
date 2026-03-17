package org.example.clothing_be.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserRoleId {
    private Long userId;
    private Long roleId;

    public UserRoleId(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
