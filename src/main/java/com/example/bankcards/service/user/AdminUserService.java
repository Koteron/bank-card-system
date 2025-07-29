package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserDto;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing users with administrator privileges.
 * Allows listing, retrieving, promoting, and deleting users.
 */
public interface AdminUserService {

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id UUID of the user to be deleted
     */
    void deleteUserById(UUID id);

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of {@link UserDto} objects
     */
    List<UserDto> getUsers();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id UUID of the user
     * @return {@link UserDto} of the found user
     */
    UserDto getUserById(UUID id);

    /**
     * Grants administrator privileges to a user.
     *
     * @param id UUID of the user to promote
     */
    void makeAdmin(UUID id);
}
