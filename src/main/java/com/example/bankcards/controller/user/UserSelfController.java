package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.*;
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
@RequestMapping("/api/user")
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
    public AuthResponseDto changeEmail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChangeEmailDto dto
    ) {
        return userSelfService.changeEmail(userDetails.getUser(), dto.newEmail());
    }

    @PatchMapping("/update/nickname")
    public UserDto changeNickname(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChangeNicknameDto dto
    ) {
        return userSelfService.changeNickname(userDetails.getUser(), dto.newNickname());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userSelfService.deleteUser(userDetails.getUser());
    }
}
