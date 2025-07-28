package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.service.user.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @DeleteMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("user-id") UUID userId) {
        adminUserService.deleteUserById(userId);
    }

    @GetMapping("/{user-id}")
    public UserDto getUserById(@PathVariable("user-id") UUID userId) {
        return adminUserService.getUserById(userId);
    }

    @PatchMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeAdmin(@PathVariable("user-id") UUID userId) {
        adminUserService.makeAdmin(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return adminUserService.getUsers();
    }
}
