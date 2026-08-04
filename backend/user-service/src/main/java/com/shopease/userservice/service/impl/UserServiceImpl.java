package com.shopease.userservice.service.impl;

import com.shopease.userservice.dto.UserDto;
import com.shopease.userservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<UserDto> getAllUsers() {

        return List.of(
                UserDto.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john@shopease.com")
                        .build(),

                UserDto.builder()
                        .id(2L)
                        .name("Jane Smith")
                        .email("jane@shopease.com")
                        .build()
        );
    }

    @Override
    public UserDto getUserById(Long id) {

        if (id == 1L) {
            return UserDto.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john@shopease.com")
                    .build();
        }

        if (id == 2L) {
            return UserDto.builder()
                    .id(2L)
                    .name("Jane Smith")
                    .email("jane@shopease.com")
                    .build();
        }

        return UserDto.builder()
                .id(id)
                .name("Unknown User")
                .email("unknown@shopease.com")
                .build();
    }
}