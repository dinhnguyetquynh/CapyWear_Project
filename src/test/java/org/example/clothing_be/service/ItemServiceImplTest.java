package org.example.clothing_be.service;

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
import org.example.clothing_be.service.serviceImpl.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getAllItems_shouldReturnPageResponse() {
        Item item = new Item();
        item.setId(1);
        item.setName("Shirt");
        item.setPrice(100);
        item.setInventoryQty(5);
        item.setUrlImg("url");

        var pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        var page = new PageImpl<>(List.of(item), pageRequest, 1);

        when(itemRepository.findAllByDeletedFalse(any()))
                .thenReturn(page);

        PageResponse<ItemRes> response = itemService.getAllItems(0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNo());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        ItemRes res = response.getContent().get(0);
        assertEquals("Shirt", res.getName());
        assertEquals(100, res.getPrice());
    }

    @Test
    void createItem_shouldSaveWhenNotExists() {
        ItemReq req = new ItemReq();
        req.setName("NewItem");
        req.setPrice(50.0);
        req.setInventoryQty(10);
        req.setUrlImg("img");

        when(itemRepository.existsByName("NewItem")).thenReturn(false);

        Item saved = new Item();
        saved.setId(10);
        saved.setName("NewItem");
        saved.setPrice(50);
        saved.setInventoryQty(10);
        saved.setUrlImg("img");

        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemRes result = itemService.createItem(req);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("NewItem", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowWhenExists() {
        ItemReq req = new ItemReq();
        req.setName("ExistItem");

        when(itemRepository.existsByName("ExistItem")).thenReturn(true);

        assertThrows(ItemAlreadyExistsException.class, () -> itemService.createItem(req));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_shouldUpdateExisting() {
        Item existing = new Item();
        existing.setId(5);
        existing.setName("Old");
        existing.setPrice(20);
        existing.setInventoryQty(2);
        existing.setUrlImg("old");

        when(itemRepository.findById(5)).thenReturn(Optional.of(existing));

        ItemUpdateReq req = new ItemUpdateReq();
        req.setName("Updated");
        req.setPrice(30.0);
        req.setInventoryQty(4);
        req.setUrlImg("new");

        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemRes res = itemService.updateItem(5, req);

        assertEquals("Updated", res.getName());
        assertEquals(30, res.getPrice());
        assertEquals(4, res.getInventoryQty());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowWhenNotFound() {
        when(itemRepository.findById(999)).thenReturn(Optional.empty());
        ItemUpdateReq req = new ItemUpdateReq();
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(999, req));
    }

    @Test
    void deleteItem_shouldSoftDeleteWhenInCartOrOrder() {
        Item item = new Item();
        item.setId(3);

        when(itemRepository.findById(3)).thenReturn(Optional.of(item));
        when(cartDetailRepository.existsByItemId(3)).thenReturn(true);
        when(orderDetailRepository.existsByItemId(3)).thenReturn(false);

        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        itemService.deleteItem(3);

        // When item is in cart/order, service should save (soft delete) and not delete
        verify(itemRepository).save(any(Item.class));
        verify(itemRepository, never()).delete(any(Item.class));
    }

    @Test
    void deleteItem_shouldHardDeleteWhenNotInCartOrOrder() {
        Item item = new Item();
        item.setId(4);

        when(itemRepository.findById(4)).thenReturn(Optional.of(item));
        when(cartDetailRepository.existsByItemId(4)).thenReturn(false);
        when(orderDetailRepository.existsByItemId(4)).thenReturn(false);

        itemService.deleteItem(4);

        verify(itemRepository).delete(item);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void findTop10Item_shouldReturnMappedList() {
        Item item = new Item();
        item.setId(7);
        item.setName("Top");
        item.setPrice(12);
        item.setInventoryQty(3);
        item.setUrlImg("u");

        when(itemRepository.findTop10ByNameContainingIgnoreCase("to")).thenReturn(List.of(item));

        var result = itemService.findTop10Item("to");

        assertEquals(1, result.size());
        assertEquals("Top", result.get(0).getName());
    }

    @Test
    void findLowStockItems_shouldReturnMappedList() {
        Item item = new Item();
        item.setId(8);
        item.setName("Low");
        item.setPrice(5);
        item.setInventoryQty(0);
        item.setUrlImg("u2");

        when(itemRepository.findLowStockItems(1)).thenReturn(List.of(item));

        var result = itemService.findLowStockItems();

        assertEquals(1, result.size());
        assertEquals("Low", result.get(0).getName());
    }

    @Test
    void getItemDetail_shouldReturnWhenExists() {
        Item item = new Item();
        item.setId(21);
        item.setName("Detail");
        item.setPrice(70);
        item.setInventoryQty(6);
        item.setUrlImg("i");

        when(itemRepository.findById(21)).thenReturn(Optional.of(item));

        ItemRes res = itemService.getItemDetail(21);
        assertEquals("Detail", res.getName());
        assertEquals(70, res.getPrice());
    }

    @Test
    void getItemDetail_shouldThrowWhenNotFound() {
        when(itemRepository.findById(404)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemDetail(404));
    }
}
