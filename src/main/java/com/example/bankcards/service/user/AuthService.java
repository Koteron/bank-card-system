package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.UserLoginDto;
import com.example.bankcards.dto.user.UserRegisterDto;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedException;

/**
 * Service implementation that handles user authentication and registration logic.
 * <p>
 * Provides functionality to register a new user, authenticate an existing user,
 * and generate JWT tokens for authenticated sessions.
 * </p>
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param userRegisterDto DTO containing email, password, and other registration data
     * @return an {@link AuthResponseDto} containing user details and JWT token
     * @throws ForbiddenException if a user with the given email already exists
     */
    AuthResponseDto register(UserRegisterDto userRegisterDto);

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param userLoginDto DTO containing email and password
     * @return an {@link AuthResponseDto} with user data and JWT token if credentials are valid
     * @throws NotFoundException if no user is found with the given email
     * @throws UnauthorizedException if the provided password is incorrect
     */
    AuthResponseDto login(UserLoginDto userLoginDto);

    /**
     * Generates a JWT token for the specified email.
     *
     * @param email the user's email
     * @return a valid JWT token
     */
    String generateToken(String email);
}
