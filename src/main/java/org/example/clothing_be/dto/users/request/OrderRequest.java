package org.example.clothing_be.dto.users.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<ItemRequest> items;

    @Getter @Setter
    public static class ItemRequest {
        private int itemId;
        private int quantity;
    }
}
