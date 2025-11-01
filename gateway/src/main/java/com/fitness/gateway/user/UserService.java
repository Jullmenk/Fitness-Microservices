package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling User Registration API for email {}", registerRequest.getEmail());

        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e->{
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        throw new RuntimeException("Bad request " + e.getMessage());
                    } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        throw new RuntimeException("Internal Server Error");
                    } else {
                        throw new RuntimeException("Unexpected error while validating user: " + e.getMessage(), e);
                    }
                });
    }

    public Mono<Boolean> validateUser(String userId) {

            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, e->{
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            throw new RuntimeException("User not found " + userId);
                        } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            throw new RuntimeException("Invalid Request ");
                        } else {
                            throw new RuntimeException("Unexpected error while validating user: " + userId, e);
                        }
                    });
    }
}
