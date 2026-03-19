package org.example.clothing_be.dto.users.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.AnyKeyJavaClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateReq {
    @Email
    @NotBlank
    private String email;
    @Size(min = 6)
    private String password;
}
