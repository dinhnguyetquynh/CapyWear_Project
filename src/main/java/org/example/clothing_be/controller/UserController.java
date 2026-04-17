package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.general.res.ApiRes;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.service.UserService;
import org.example.clothing_be.service.serviceImpl.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ApiRes<UserRes> getProfileUser(){
        UserRes res = userService.getProfileUser();
        return ApiRes.success(200,res,"Lấy thông tin người dùng thành công");
    }
}
