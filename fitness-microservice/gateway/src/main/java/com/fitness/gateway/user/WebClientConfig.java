package com.fitness.gateway.user;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig
{
    @Bean
    @LoadBalanced  //it allows the rest client to resolve the service name via eureka.So, the registered microservices on eureka can run anywhere but the name of the service will be same so it can be accessed by that.
    public WebClient.Builder webClientBuilder()
    {
        return WebClient.builder();
    }

    //this webclient points to the userService and by this we can communicate with  the userService microservice.
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientbuilder)
    {
        return webClientbuilder
                .baseUrl("http://USER-SERVICE") //this is the name of the service which we register on eureka server
                .build();
    }
}
