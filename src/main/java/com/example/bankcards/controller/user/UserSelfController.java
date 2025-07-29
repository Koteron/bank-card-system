package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.user.UserSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User - Self")
public class UserSelfController {
    private static final Logger log = LoggerFactory.getLogger(UserSelfController.class);
    private final UserSelfService userSelfService;

    @Operation(
            summary = "Change password",
            description = "Changes user's password and checks the old given one",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Old and new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Password was changed",
                            content = @Content()),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "403",
                            description = "Given old password is invalid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @PatchMapping("/update/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid OldNewPasswordDto oldNewPasswordDto) {
        log.debug("Request received: Change password of user with email {}", userDetails.getUser().getEmail());
        userSelfService.changePassword(userDetails.getUser(), oldNewPasswordDto);
    }

    @Operation(
            summary = "Change email",
            description = "Changes user's email and checks it for uniqueness",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New email",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Email was changed. Auth response was returned to refresh token",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = AuthResponseDto.class)
                    )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "403",
                            description = "Email is already in use",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @PatchMapping("/update/email")
    public AuthResponseDto changeEmail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChangeEmailDto dto
    ) {
        log.debug("Request received: Change email of user with email {}", userDetails.getUser().getEmail());
        return userSelfService.changeEmail(userDetails.getUser(), dto.newEmail());
    }

    @Operation(
            summary = "Change nickname",
            description = "Changes user's nickname",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New nickname",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Nickname was changed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @PatchMapping("/update/nickname")
    public UserDto changeNickname(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid ChangeNicknameDto dto
    ) {
        log.debug("Request received: Change nickname of user with email {}", userDetails.getUser().getEmail());
        return userSelfService.changeNickname(userDetails.getUser(), dto.newNickname());
    }

    @Operation(
            summary = "Delete account",
            description = "Deletes user's account",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "User was deleted",
                            content = @Content()),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.debug("Request received: Delete account of user with email {}", userDetails.getUser().getEmail());
        userSelfService.deleteUser(userDetails.getUser());
    }
}
