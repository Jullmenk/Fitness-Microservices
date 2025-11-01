package com.fitness.activityservice.mapper;

import com.fitness.activityservice.dto.ActivityResponseDTO;
import com.fitness.activityservice.model.Activity;

public class ActivityMapper {

    public  ActivityMapper(){

    }

    public static ActivityResponseDTO toResponse(Activity activity){
        ActivityResponseDTO activityResponseDTO = new ActivityResponseDTO();
        activityResponseDTO.setId(activity.getId());
        activityResponseDTO.setId(activity.getId());
        activityResponseDTO.setType(activity.getType());
        activityResponseDTO.setDuration(activity.getDuration());
        activityResponseDTO.setAdditionalMetrics(activity.getAdditionalMetrics());
        activityResponseDTO.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponseDTO.setUserId(activity.getUserId());
        activityResponseDTO.setCreatedAt(activity.getCreatedAt());
        activityResponseDTO.setUpdatedAt(activity.getUpdatedAt());

        return activityResponseDTO;
    }
}
