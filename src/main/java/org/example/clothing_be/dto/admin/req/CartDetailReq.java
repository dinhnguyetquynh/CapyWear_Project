package org.example.clothing_be.dto.admin.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailReq {
    @NotBlank
    private Integer itemId;
    @NotBlank
    private Integer quantity;
}
