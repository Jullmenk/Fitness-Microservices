package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendations;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendations> getUserRecommendations(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendations   getActivityRecommendations(String activityId) {
         return recommendationRepository.findByActivityId(activityId)
                 .orElseThrow(()-> new RuntimeException("Activity not found" + activityId));
    }
}
