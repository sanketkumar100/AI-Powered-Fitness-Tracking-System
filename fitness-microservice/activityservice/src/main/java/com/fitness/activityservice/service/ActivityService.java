package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService
{
    private final ActivityRepository activityRepository;

    private final UserValidationService userValidationService;

    private final RabbitTemplate rabbitTemplate;

    //getting rabbitMQ properties from the application.properties
    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request)
    {
        //vlaidating the user by calling the userValidationService.validateUser() method which will call the userService to validate the user by using Webclient.
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser)
        {
            throw new RuntimeException("Invalid user" +request.getUserId());
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        //System.out.println("before save");
        Activity savedActivity = activityRepository.save(activity);
        //System.out.println("After save");

        //publish to RabbitMq for AI Processing
        try
        {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        }
        catch (Exception e)
        {
            log.error("Failed to publish activity to RabbitMQ :", e);

        }



        return mapToResponse(savedActivity);

    }

    private ActivityResponse mapToResponse(Activity activity)
    {
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .additionalMetrics(activity.getAdditionalMetrics())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();

    }

    public List<ActivityResponse> getUserActivities(String userId)
    {
        List<Activity> activities = activityRepository.findByUserId(userId);
        //now here we have list of activity so, we will convert each by using the function mapToResponse().
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId)
    {
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity not found with id:" +activityId));
    }
}
