package com.fitness.activityservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;


@Document(collection = "activities") //it is coming from the mongoDB dependency
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity
{
    @Id
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;

    @Field("metrics")
    private Map<String, Object> additionalMetrics;   //if we import metrics from any source.

    @CreatedDate           //here, creratedTimeStamp and updatedTimeStamp are not used because we are using MongoDB instead of SQL database. So we have to use @CreatedDate and @LastModifiedDate annotations.
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
