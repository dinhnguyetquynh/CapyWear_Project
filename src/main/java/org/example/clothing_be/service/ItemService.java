package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.entity.Item;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Page<ItemRes> getAllItems(int page, int size);
    ItemRes createItem(ItemReq req);
    ItemRes updateItem(Integer id, ItemUpdateReq req);
    void deleteItem(Integer id);
    List<ItemRes> findTop10Item(String name);
    List<ItemRes> findLowStockItems();
}
