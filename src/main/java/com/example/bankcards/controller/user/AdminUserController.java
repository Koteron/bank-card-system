package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@Tag(name = "Admin - Users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Operation(
            summary = "Delete a user",
            description = "Deletes user with given id",
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
    @DeleteMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(
            @Parameter(description = "UUID of the user",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("user-id") UUID userId
    ) {
        adminUserService.deleteUserById(userId);
    }

    @Operation(
            summary = "Get a user",
            description = "Returns the user with given id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User was returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
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
    @GetMapping("/{user-id}")
    public UserDto getUserById(
            @Parameter(description = "UUID of the user",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("user-id") UUID userId
    ) {
        return adminUserService.getUserById(userId);
    }

    @Operation(
            summary = "Make user an admin",
            description = "Changes the role of a given user to admin",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "User role was changed to admin",
                            content = @Content()),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
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
    @PatchMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeAdmin(
            @Parameter(description = "UUID of the user",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("user-id") UUID userId
    ) {
        adminUserService.makeAdmin(userId);
    }

    @Operation(
            summary = "Get the list of users",
            description = "Returns all existing users",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List of UserDtos was returned",
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
    @GetMapping
    public List<UserDto> getUsers() {
        return adminUserService.getUsers();
    }
}
