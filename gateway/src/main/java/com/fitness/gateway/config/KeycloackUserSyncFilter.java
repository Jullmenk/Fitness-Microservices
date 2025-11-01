package com.fitness.gateway.config;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloackUserSyncFilter implements WebFilter
{
    private final UserService  userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeycloakId();
        }

        if(userId != null && token != null){
            log.info("Adding new user");
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist ->{
                        if(!exist){
                            if(registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .doOnSuccess(u -> log.info("User registered: {}", u.getEmail()))
                                        .doOnError(e -> log.error("Error registering user", e));
                            }
                            else {
                                return Mono.empty();
                            }
                        }else {
                            log.info("User already exist, skipping sync");
                            return Mono.empty();
                        }
                    })
                    .then(
                            Mono.defer(()->{
                                ServerHttpRequest request = exchange.getRequest().mutate()
                                        .header("X-User-ID", finalUserId)
                                        .build();
                                    return chain.filter(exchange.mutate().request(request).build());
                            })
                    );
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token){
        try{
            String tokenWithouBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithouBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setFirstName(claims.getStringClaim("family_name"));
            registerRequest.setLastName(claims.getStringClaim("given_name"));
            registerRequest.setPassword("dummy@123123");
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            return registerRequest;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

}
