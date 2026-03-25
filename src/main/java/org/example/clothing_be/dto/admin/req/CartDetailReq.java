package org.example.clothing_be.dto.admin.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailReq {
    private Integer itemId;
    private Integer quantity;
}
