package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService
{
    private final WebClient userServiceWebclient; //getting the instance of webclient which we create

    public boolean validateUser(String userId)
    {
        log.info("Calling user validation API for userId: {}", userId);

        try
        {
            //sending get request to the user service
            return userServiceWebclient.get()
                    .uri("api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }
        catch( WebClientResponseException e)
        {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
            {
                throw new RuntimeException("User not found" +userId);
            }
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw new RuntimeException("Invalid Request" +userId);
            }
        }
        return false;

    }
}
