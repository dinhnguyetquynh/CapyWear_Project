package org.example.clothing_be.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public Page<ItemRes> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        Page<Item> itemPage = itemRepository.findAll(pageable);

        return itemPage.map(item -> {
            ItemRes res = new ItemRes();
            res.setId(item.getId());
            res.setName(item.getName());
            res.setPrice(item.getPrice());
            res.setInventoryQty(item.getInventoryQty());
            res.setUrlImg(item.getUrlImg());
            return res;
        });
    }
}
