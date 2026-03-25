package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.exception.ItemAlreadyExistsException;
import org.example.clothing_be.exception.ItemNotFoundException;
import org.example.clothing_be.repository.CartDetailRepository;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.OrderDetailRepository;
import org.example.clothing_be.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;

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

    @Transactional
    @Override
    public ItemRes createItem(ItemReq req) {
        if (itemRepository.existsByName(req.getName())){
            throw new ItemAlreadyExistsException();
        }
        Item newItem = itemRepository.save(toEntity(req));
        return toDTO(newItem);
    }

    @Transactional
    @Override
    public ItemRes updateItem(Integer id, ItemUpdateReq req) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException());

        if (req.getName() != null) existingItem.setName(req.getName());
        if (req.getPrice() != null) existingItem.setPrice(req.getPrice());
        if (req.getUrlImg() != null) existingItem.setUrlImg(req.getUrlImg());
        if (req.getInventoryQty() != null) existingItem.setInventoryQty(req.getInventoryQty());

        return toDTO(itemRepository.save(existingItem));
    }

    @Override
    @Transactional
    public void deleteItem(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException());

        boolean isInCart = cartDetailRepository.existsByItemId(id);
        boolean isInOrder = orderDetailRepository.existsByItemId(id);
        if (isInCart || isInOrder) {
            item.setDeleted(true);
            itemRepository.save(item);

        } else {
            itemRepository.delete(item);
        }
    }


    private Item toEntity(ItemReq req){
        Item item = new Item();
        item.setName(req.getName());
        item.setPrice(req.getPrice());
        item.setUrlImg(req.getUrlImg());
        item.setInventoryQty(req.getInventoryQty());
        return item;
    }
    private ItemRes toDTO(Item item){
        ItemRes res = new ItemRes();
        res.setId(item.getId());
        res.setName(item.getName());
        res.setPrice(item.getPrice());
        res.setInventoryQty(item.getInventoryQty());
        res.setUrlImg(item.getUrlImg());
        return res;
    }

}
