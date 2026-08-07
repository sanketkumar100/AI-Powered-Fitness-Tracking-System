package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class UserService
{
    private final UserRepository repository;
    public UserResponse register(RegisterRequest request)
    {
        if(repository.existsByEmail(request.getEmail()))
        {
            User exsistingUser = repository.findByEmail(request.getEmail());;

            UserResponse userResponse = new UserResponse();
            userResponse.setId(exsistingUser.getId());
            userResponse.setKeyCloakId(exsistingUser.getKeyCloakId());
            userResponse.setPassword(exsistingUser.getPassword());
            userResponse.setEmail(exsistingUser.getEmail());
            userResponse.setFirstName(exsistingUser.getFirstName());
            userResponse.setLastName(exsistingUser.getLastName());
            userResponse.setCreatedAt(exsistingUser.getCreatedAt());
            userResponse.setUpdatedAt(exsistingUser.getUpdatedAt());

            return userResponse;

        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeyCloakId(request.getKeyCloakId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setKeyCloakId(savedUser.getKeyCloakId());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());

        return userResponse;

    }

    public UserResponse getUserProfile(String userId)
    {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }

    public Boolean existByUserId(String userId)
    {
        log.info("Calling user validation API for userId: {}", userId);
        return repository.existsByKeyCloakId(userId); //validating by using keycloak user id.
    }

}
