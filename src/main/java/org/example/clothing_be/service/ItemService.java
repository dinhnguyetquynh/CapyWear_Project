package org.example.clothing_be.service;

import org.example.clothing_be.dto.general.res.ItemRes;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Page<ItemRes> getAllItems(int page, int size);
}
