package com.fitness.userservice.mapper;

import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;

public class UserMapper {

    private UserMapper() {
        // evita instanciar esta classe utilitária
    }

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setLastName(user.getLastName());
        userResponse.setId(user.getId());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
