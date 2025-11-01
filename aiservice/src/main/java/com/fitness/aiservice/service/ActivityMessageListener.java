package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendations;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAiService activityAiService;
    private final RecommendationRepository recommendationRepository;
    @RabbitListener(queues = "activity.queue")
    public void proccessActivity(Activity activity) {
        log.info("Received message: {}", activity.getId());
//        log.info("Generated recommendations {}", activityAiService.generateRecommendation(activity));
        Recommendations rec = activityAiService.generateRecommendation(activity);
        recommendationRepository.save(rec);
    }
}
