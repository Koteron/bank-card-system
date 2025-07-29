package com.example.bankcards.service.user;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with email %s not found", email)));

    }

    @Override
    public User getEntityById(UUID id) {
        return userRepository.findById(id).orElseThrow(()->new NotFoundException("User with id %s not found"));
    }

}
