package com.fitness.gateway;


import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter
{
    //filter that intercepts every request passes through it, then it will get and decode the jwt token and also check in the usermicroservice if the user exists or not.

    private final UserService userService;

    //defining the filter chain, which will be executed for every request that passes through the api-gateway and the user id, authorization header etc can be done here.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain)
    {
        //getting the header
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = getUserDetails(token);

        if (userId==null)
        {
            userId = registerRequest.getKeyCloakId(); //if user id is not present in the header, then we will get it from the token
        }

        //now, checking the user
        if (userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            //register user

                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }
                            else {
                            return Mono.empty();
                           }
                    }
                        else {
                         log.info("user already exist, skipping Sync.");
                          return Mono.empty();
                }
                    })
                    .then(Mono.defer(() -> {                //mutating the request to explicitly set the header, so that it can be used in the downstream microservices.
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return chain.filter(exchange); //if user id and token is missing continue the filter chain

    }

    private RegisterRequest getUserDetails(String token)
    {
        try
        {
            String tokenWithoutBearer = token.replace("Bearer", "").trim(); //removing the Bearer prefix from the token
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeyCloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123123"); //we can remove this field because anyways the password is being set by keycloak
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            return registerRequest;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
