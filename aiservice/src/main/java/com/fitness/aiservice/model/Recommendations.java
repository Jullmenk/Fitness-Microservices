package com.fitness.aiservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recommendations")
@Data
@Builder
public class Recommendations {
    @Id
    private String id;
    private String userId;
    private String activityId;
    private String activityType;
    private String recommendation;
    List<String> improvements;
    List<String> suggestions;
    List<String> safety;

    @CreatedDate
    private LocalDateTime createdAt;
}
