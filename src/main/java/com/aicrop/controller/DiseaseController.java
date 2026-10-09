package com.aicrop.controller;

import com.aicrop.model.Disease;
import com.aicrop.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disease")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @GetMapping("/all")
    public ResponseEntity<List<Disease>> getAllDiseases() {
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }

    @GetMapping("/crop/{cropName}")
    public ResponseEntity<List<Disease>> getByCrop(@PathVariable String cropName) {
        return ResponseEntity.ok(diseaseService.getDiseasesByCrop(cropName));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Disease>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(diseaseService.searchDiseases(keyword));
    }
}
