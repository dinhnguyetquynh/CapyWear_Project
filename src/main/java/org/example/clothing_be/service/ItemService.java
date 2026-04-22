package org.example.clothing_be.service;

import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.dto.general.res.PageResponse;

import java.util.List;

public interface ItemService {
//    Page<ItemRes> getAllItems(int page, int size);
    PageResponse<ItemRes> getAllItems(int page, int size);
    ItemRes createItem(ItemReq req);
    ItemRes updateItem(Integer id, ItemUpdateReq req);
    void deleteItem(Integer id);
    List<ItemRes> findTop10Item(String name);
    List<ItemRes> findLowStockItems();
    ItemRes getItemDetail(int itemId);
    PageResponse<ItemRes> findByPriceRange(int page, int size, Double minPrice,Double maxPrice);
}
