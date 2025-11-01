package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequestDTO;
import com.fitness.activityservice.dto.ActivityResponseDTO;
import com.fitness.activityservice.mapper.ActivityMapper;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponseDTO trackActivity(ActivityRequestDTO request ) {
        boolean isValid = userValidationService.validateUser(request.getUserId());
        if (!isValid) {
            throw new RuntimeException("Invalid user");
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .caloriesBurned(request.getCaloriesBurned())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        Activity savedActivity = activityRepository.save(activity);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Error occurred while sending activity to RabbitMQ",e );
        }

        return ActivityMapper.toResponse(savedActivity);
    }

    public List<ActivityResponseDTO> getActivity(String userId) {
        List <Activity> activities = activityRepository.findByUserId(userId);
           return activities.stream()
                   .map(ActivityMapper::toResponse)
                   .collect(Collectors.toList());
    }

    public ActivityResponseDTO getOneActivity(String activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + activityId));
        return ActivityMapper.toResponse(activity);
    }
}
