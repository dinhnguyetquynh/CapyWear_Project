package org.example.clothing_be.dto.users.respone;

import lombok.*;
import org.example.clothing_be.enums.Status;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRes {
    private Long id;
    private String email;
    private LocalDate created_at;
    private String status;
    private String imgUrl;
}
