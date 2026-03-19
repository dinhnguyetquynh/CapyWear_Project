package org.example.clothing_be.dto.users.respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.clothing_be.enums.Status;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRes {
    private Long id;
    private String email;
    private LocalDate created_at;
    private Status status;
    private String imgUrl;
}
