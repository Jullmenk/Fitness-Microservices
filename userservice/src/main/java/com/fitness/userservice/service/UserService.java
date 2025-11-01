package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.mapper.UserMapper;
import com.fitness.userservice.model.User;
import com.fitness.userservice.respository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

     private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(""));
        return UserMapper.toResponse(user);
    }

    public UserResponse registerUser(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            User user = userRepository.findByEmail(request.getEmail());
            return UserMapper.toResponse(user);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setKeycloakId(request.getKeycloakId());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

//    public Boolean existByUserId(String userId) {
//        return userRepository.existsById(userId);
//    }

    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Boolean existByKeycloakId(String userId) {
        return userRepository.existsByKeycloakId(userId);
    }
}
