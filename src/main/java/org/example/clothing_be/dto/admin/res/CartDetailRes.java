package org.example.clothing_be.dto.admin.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clothing_be.dto.general.res.ItemRes;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailRes {
    private int id;
    private double purchasePrice;
    private int quantity;
    private double totalItem;
    private LocalDate dateAdd;
    private ItemRes itemRes;
}
