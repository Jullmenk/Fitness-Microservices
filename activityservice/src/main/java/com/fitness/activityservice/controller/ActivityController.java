package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequestDTO;
import com.fitness.activityservice.dto.ActivityResponseDTO;
import com.fitness.activityservice.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> trackActivity(@RequestBody ActivityRequestDTO request){
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> getActivity(@RequestHeader("X-User-ID")  String userId ){
        return ResponseEntity.ok(activityService.getActivity(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponseDTO> getOneActivity(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getOneActivity(activityId));
    }
}
