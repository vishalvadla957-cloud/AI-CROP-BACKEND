package com.aicrop.controller;

import com.aicrop.dto.*;
import com.aicrop.service.CropRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/crop")
public class CropController {

    @Autowired
    private CropRecommendationService recommendationService;

    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@Valid @RequestBody SoilInputDTO input,
                                       Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        RecommendationDTO result = recommendationService.recommend(input, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCrops() {
        return ResponseEntity.ok(recommendationService.getAllCrops());
    }

    @GetMapping("/info/{cropName}")
    public ResponseEntity<?> getCropInfo(@PathVariable String cropName) {
        Map<String, Object> info = recommendationService.getCropInfo(cropName);
        if (info.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication auth) {
        Map<String, Object> stats = recommendationService.getDashboardStats(auth.getName());
        return ResponseEntity.ok(stats);
    }
}
