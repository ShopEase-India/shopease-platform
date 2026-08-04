package com.shopease.userservice.controller;

import com.shopease.common.response.ApiResponse;
import com.shopease.userservice.dto.UserDto;
import com.shopease.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for User operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserDto>> getAllUsers() {

        return ApiResponse.success(
                userService.getAllUsers(),
                "Users retrieved successfully"
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUserById(@PathVariable("id") Long id) {
        return ApiResponse.success(
                userService.getUserById(id),
                "User retrieved successfully"
        );
    }

}