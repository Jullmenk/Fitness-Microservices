package com.fitness.userservice.respository;

import com.fitness.userservice.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(@NotBlank(message = "Email is required") String email);

    User findByEmail(@NotBlank(message = "Email is required") String email);

    Boolean existsByKeycloakId(String keycloakId);
}
