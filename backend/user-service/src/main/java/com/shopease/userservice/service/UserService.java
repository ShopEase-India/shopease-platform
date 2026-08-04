package com.shopease.userservice.service;

import com.shopease.userservice.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

}