package org.example.clothing_be.dto.users.respone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDTO {
    private String itemName;
    private int quantity;
    private double price;
    private double total;
}
