package com.example.bankcards.service.user;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;

import java.util.UUID;

/**
 * General service for usage by other services.
 * <p>
 * Provides methods to retrieve {@link User} entities by email or ID.
 * Throws {@link NotFoundException} if the user is not found.
 * </p>
 */
public interface UserService {

    /**
     * Retrieves a {@link User} entity by its email address.
     *
     * @param email the email of the user to retrieve
     * @return the {@link User} entity associated with the given email
     * @throws NotFoundException if no user with the given email is found
     */
    User getEntityByEmail(String email);

    /**
     * Retrieves a {@link User} entity by its ID.
     *
     * @param id the UUID of the user to retrieve
     * @return the {@link User} entity associated with the given ID
     * @throws NotFoundException if no user with the given ID is found
     */
    User getEntityById(UUID id);
}
