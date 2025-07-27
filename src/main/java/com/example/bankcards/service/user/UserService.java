package com.example.bankcards.service.user;

import com.example.bankcards.entity.User;

import java.util.UUID;

public interface UserService {
    User getEntityByEmail(String email);
    User getEntityById(UUID id);
}
