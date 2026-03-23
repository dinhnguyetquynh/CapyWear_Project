package org.example.clothing_be.dto.general.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRes {
    private int id;
    private String name;
    private String urlImg;
    private double price;
    private int inventoryQty;
}
