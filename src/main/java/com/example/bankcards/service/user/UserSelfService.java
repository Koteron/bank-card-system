package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.AuthResponseDto;
import com.example.bankcards.dto.user.OldNewPasswordDto;
import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenException;

/**
 * Service implementation for self-service user operations.
 * <p>
 * This service provides functionality for authenticated users to manage
 * their own account details, such as changing passwords, emails, nicknames,
 * and deleting their own account.
 * </p>
 */
public interface UserSelfService {

    /**
     * Changes the password for the given user.
     *
     * @param user the authenticated user
     * @param oldNewPasswordDto object containing the old and new password
     * @throws ForbiddenException if the provided old password does not match the current password
     */
    void changePassword(User user, OldNewPasswordDto oldNewPasswordDto);

    /**
     * Changes the email address of the user and returns a new authentication token.
     *
     * @param user the authenticated user
     * @param newEmail the new email address to set
     * @return an {@link AuthResponseDto} containing updated user info and a new token
     * @throws ForbiddenException if the email is already in use
     */
    AuthResponseDto changeEmail(User user, String newEmail);

    /**
     * Updates the nickname of the user.
     *
     * @param user the authenticated user
     * @param newNickname the new nickname to assign
     * @return the updated {@link UserDto}
     */
    UserDto changeNickname(User user, String newNickname);

    /**
     * Deletes the user account from the system.
     *
     * @param user the authenticated user
     */
    void deleteUser(User user);
}
