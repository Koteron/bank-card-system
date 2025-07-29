package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserLoginDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.service.user.AuthService;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Operation(
            summary = "Register a user",
            description = "Registers user with given credentials and send it back with jwt-token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegisterDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "User was created and returned along with jwt token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )),
                    @ApiResponse(responseCode = "403",
                            description = "User already exists",
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
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        log.debug("Request received: Register user with email {}", userRegisterDto.email());
        return authService.register(userRegisterDto);
    }

    @Operation(
            summary = "Login a user",
            description = "Logs user in by given credentials and send it back with jwt-token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User was logged in and returned along with jwt token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )),
                    @ApiResponse(responseCode = "404",
                            description = "User with given credentials not found",
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
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody UserLoginDto userLoginDto) {
        log.debug("Request received: Log in user with email {}", userLoginDto.email());
        return authService.login(userLoginDto);
    }
}
