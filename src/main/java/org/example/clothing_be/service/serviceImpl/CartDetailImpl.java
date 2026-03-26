package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.admin.req.CartDetailReq;
import org.example.clothing_be.dto.admin.res.CartDetailRes;

import org.example.clothing_be.dto.general.res.ItemRes;
import org.example.clothing_be.entity.Cart;
import org.example.clothing_be.entity.CartDetail;
import org.example.clothing_be.entity.Item;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.exception.*;
import org.example.clothing_be.repository.CartDetailRepository;
import org.example.clothing_be.repository.CartRepository;
import org.example.clothing_be.repository.ItemRepository;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartDetailImpl implements CartService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserRepository userRepository;

    //Function : add an item in cart
    @Override
    @Transactional
    public CartDetailRes addItem(Long userId,CartDetailReq req) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(()->new UserNotFoundException());

        if(!currentUser.getId().equals(userId)){
            throw new ForbiddenActionException();
        }
        //Dành cho trường hợp sản phẩm đã được xoá hoặc ngưng bán rồi nhưng UI chưa load kịp
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(()-> new ItemNotFoundException());
        if(item.isDeleted()){
            throw new ProductNotAvailableException();
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(()->{
                    Cart cart1 = new Cart();
                    cart1.setUser(currentUser);
                    cart1.setTotalCart(0);
                    return cartRepository.save(cart1);
                });
        Optional<CartDetail> existingDetailOpt = cartDetailRepository.findByCartIdAndItemId(cart.getId(),item.getId());
        CartDetail cartDetail;
        if(existingDetailOpt.isPresent()){
            cartDetail = existingDetailOpt.get();
            cartDetail.setQuantity(cartDetail.getQuantity()+req.getQuantity());
            cartDetail.setPurchasePrice(item.getPrice());
            cartDetail.setTotalItem(cartDetail.getQuantity()*item.getPrice());
        }else{
            cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setItem(item);
            cartDetail.setPurchasePrice(item.getPrice());
            cartDetail.setQuantity(req.getQuantity());
            cartDetail.setTotalItem(req.getQuantity()*item.getPrice());
            cartDetail.setDateAdd(LocalDate.now());
        }
        CartDetail savedDetail = cartDetailRepository.save(cartDetail);
        CartDetailRes cartDetailRes = toCartDetailDTO(savedDetail);
        recalculateCartTotal(cart);

        return cartDetailRes;
    }

    //Function : get all cart detail of user
    @Override
    public List<CartDetailRes> getAllByUser(Long userId, int page, int size) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(()->new UserNotFoundException());

        if(!currentUser.getId().equals(userId)){
            throw new ForbiddenActionException();
        }
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        List<CartDetail> cartDetails = cartDetailRepository.findAllByUserIdWithItem(userId);
        List<CartDetailRes> cartDetailResList = new ArrayList<>();
        for(CartDetail cartDetail:cartDetails){
            CartDetailRes res = toCartDetailDTO(cartDetail);
            cartDetailResList.add(res);
        }
        return cartDetailResList;
    }

    @Override
    @Transactional
    public CartDetailRes updateCartDetail(Integer id,Integer quantity) {
        CartDetail cartDetail = cartDetailRepository.findById(id)
                .orElseThrow(()-> new CartDetailNotFound("Không tìm thấy cart detail"+":id"));
        cartDetail.setQuantity(quantity);
        cartDetail.setTotalItem(cartDetail.getQuantity()*cartDetail.getPurchasePrice());
        CartDetail updateCart = cartDetailRepository.save(cartDetail);

        Cart cart = cartDetail.getCart();
        recalculateCartTotal(cart);
        return toCartDetailDTO(updateCart);
    }


    @Override
    @Transactional
    public void deleteCartDetail(Integer cartDetailId) {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(()-> new CartDetailNotFound(""));
        Integer cartId = cartDetail.getCart().getId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy cart có id: "+cartId));
        double newTotal = cart.getTotalCart()-cartDetail.getTotalItem();
        cart.setTotalCart(Math.max(0, newTotal));

        cartDetailRepository.delete(cartDetail);
        cartRepository.save(cart);
    }

    private void  recalculateCartTotal(Cart cart){
        List<CartDetail> cartDetails = cartDetailRepository.findByCartId(cart.getId());
        double totalCartPrice = cartDetails.stream()
                .mapToDouble(CartDetail::getTotalItem)
                .sum();
        cart.setTotalCart(totalCartPrice);
        cartRepository.save(cart);
    }
    private CartDetailRes toCartDetailDTO(CartDetail cartDetail){
        CartDetailRes res = new CartDetailRes();
        res.setId(cartDetail.getId());
        res.setPurchasePrice(cartDetail.getItem().getPrice());
        res.setQuantity(cartDetail.getQuantity());
        res.setTotalItem(cartDetail.getTotalItem());
        res.setDateAdd(cartDetail.getDateAdd());
        ItemRes itemRes = toItemDTO(cartDetail.getItem());
        res.setItemRes(itemRes);
        return res;
    }
    private ItemRes toItemDTO(Item item){
        ItemRes res = new ItemRes();
        res.setId(item.getId());
        res.setName(item.getName());
        res.setPrice(item.getPrice());
        res.setInventoryQty(item.getInventoryQty());
        res.setUrlImg(item.getUrlImg());
        return res;
    }

}
