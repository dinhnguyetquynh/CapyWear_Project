package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.ItemReq;
import org.example.clothing_be.dto.admin.req.ItemUpdateReq;
import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.dto.general.res.PageResponse;
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

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public PageResponse<ItemRes> findByPriceRange(int page, int size, Double minPrice, Double maxPrice) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Item> itemPage = itemRepository.findByPriceRange(minPrice,maxPrice,pageable);

        // 1. Chuyển đổi từ Item sang ItemRes (vẫn dùng map của Page)
        List<ItemRes> content = itemPage.map(item -> {
            ItemRes res = new ItemRes();
            res.setId(item.getId());
            res.setName(item.getName());
            res.setPrice(item.getPrice());
            res.setInventoryQty(item.getInventoryQty());
            res.setUrlImg(item.getUrlImg());
            return res;
        }).getContent(); // Lấy list ra từ Page

        // 2. Build đối tượng PageResponse đã tạo ở Bước 1
        return PageResponse.<ItemRes>builder()
                .content(content)
                .pageNo(itemPage.getNumber())
                .pageSize(itemPage.getSize())
                .totalElements(itemPage.getTotalElements())
                .totalPages(itemPage.getTotalPages())
                .last(itemPage.isLast())
                .build();
    }


    @Override
    public PageResponse<ItemRes> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Item> itemPage = itemRepository.findAllByDeletedFalse(pageable);

        // 1. Chuyển đổi từ Item sang ItemRes (vẫn dùng map của Page)
        List<ItemRes> content = itemPage.map(item -> {
            ItemRes res = new ItemRes();
            res.setId(item.getId());
            res.setName(item.getName());
            res.setPrice(item.getPrice());
            res.setInventoryQty(item.getInventoryQty());
            res.setUrlImg(item.getUrlImg());
            return res;
        }).getContent(); // Lấy list ra từ Page

        // 2. Build đối tượng PageResponse đã tạo ở Bước 1
        return PageResponse.<ItemRes>builder()
                .content(content)
                .pageNo(itemPage.getNumber())
                .pageSize(itemPage.getSize())
                .totalElements(itemPage.getTotalElements())
                .totalPages(itemPage.getTotalPages())
                .last(itemPage.isLast())
                .build();
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

    @Override
    public List<ItemRes> findTop10Item(String name) {
        List<Item> itemList = itemRepository.findTop10ByNameContainingIgnoreCase(name);
        List<ItemRes> itemResList = new ArrayList<>();
        for(Item item: itemList){
            ItemRes res = toDTO(item);
            itemResList.add(res);
        }
        return itemResList;
    }

    @Override
    public List<ItemRes> findLowStockItems() {
        List<Item> itemList = itemRepository.findLowStockItems(1);

        List<ItemRes> itemResList = new ArrayList<>();
        for(Item item:itemList){
            ItemRes res = toDTO(item);
            itemResList.add(res);
        }
        return itemResList;
    }

    @Override
    public ItemRes getItemDetail(int itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new ItemNotFoundException());
        return toDTO(item);
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
