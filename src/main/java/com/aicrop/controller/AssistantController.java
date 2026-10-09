package com.aicrop.controller;

import com.aicrop.dto.AiQueryDTO;
import com.aicrop.service.GroqService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    @Autowired
    private GroqService groqService;

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@Valid @RequestBody AiQueryDTO query) {
        String mode = query.getMode() != null ? query.getMode() : "detailed";
        String response = groqService.askAi(query.getQuery(), query.getContext(), mode);
        return ResponseEntity.ok(Map.of(
            "query", query.getQuery(),
            "response", response,
            "mode", mode,
            "source", "KrishiMitra Groq AI Assistant"
        ));
    }
}
