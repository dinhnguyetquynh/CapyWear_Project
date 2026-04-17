package org.example.clothing_be.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.clothing_be.dto.users.respone.UserRes;
import org.example.clothing_be.entity.User;
import org.example.clothing_be.exception.UserNotFoundException;
import org.example.clothing_be.repository.UserRepository;
import org.example.clothing_be.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public UserRes getProfileUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException());
        return mapDTO(user);
    }

    private UserRes mapDTO(User user){
       return UserRes.builder()
               .id(user.getId())
               .email(user.getEmail())
               .created_at(user.getCreated_at())
               .status(user.getStatus().name())
               .imgUrl(user.getImgUrl())
               .build();
    }
}
