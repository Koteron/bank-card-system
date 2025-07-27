package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.user.UserSelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserSelfController {
    private final UserSelfService userSelfService;

    @PatchMapping("/update/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid OldNewPasswordDto oldNewPasswordDto) {
        userSelfService.changePassword(userDetails.getUser(), oldNewPasswordDto);
    }

    @PatchMapping("/update/email")
    public UserDto changeEmail(
            @AuthenticationPrincipal User user,
            @RequestParam String email) {
        return userSelfService.changeEmail(user, email);
    }

    @PatchMapping("/update/nickname")
    public UserDto changeNickname(
            @AuthenticationPrincipal User user,
            @RequestParam String firstName) {
        return userSelfService.changeNickname(user, firstName);
    }

    @DeleteMapping
    public void deleteUser(@AuthenticationPrincipal User user) {
        userSelfService.deleteUser(user);
    }
}
