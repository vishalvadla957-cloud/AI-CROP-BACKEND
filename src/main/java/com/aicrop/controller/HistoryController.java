package com.aicrop.controller;

import com.aicrop.model.FarmingHistory;
import com.aicrop.model.User;
import com.aicrop.repository.FarmingHistoryRepository;
import com.aicrop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private FarmingHistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getHistory(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        List<FarmingHistory> history = historyRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(history);
    }

    @PatchMapping("/{id}/outcome")
    public ResponseEntity<?> updateOutcome(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        Optional<FarmingHistory> opt = historyRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        FarmingHistory h = opt.get();
        if (!h.getUser().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        if (body.containsKey("outcome")) h.setOutcome(body.get("outcome"));
        if (body.containsKey("actualCropGrown")) h.setActualCropGrown(body.get("actualCropGrown"));
        historyRepository.save(h);
        return ResponseEntity.ok(h);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id, Authentication auth) {
        Optional<FarmingHistory> opt = historyRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        FarmingHistory h = opt.get();
        if (!h.getUser().getUsername().equals(auth.getName())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        historyRepository.delete(h);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}
