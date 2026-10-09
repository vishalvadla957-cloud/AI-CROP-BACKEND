package com.aicrop.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "farming_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Soil Parameters
    @Column(nullable = false)
    private double nitrogen;

    @Column(nullable = false)
    private double phosphorus;

    @Column(nullable = false)
    private double potassium;

    @Column(nullable = false)
    private double ph;

    // Climate Parameters
    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private double humidity;

    @Column(nullable = false)
    private double rainfall;

    // Recommendation Results
    @Column(nullable = false)
    private String recommendedCrop;

    private String alternativeCrop1;

    private String alternativeCrop2;

    @Column(nullable = false)
    private double confidenceScore;

    // Additional info
    private String season;

    private String notes;

    private String actualCropGrown;

    private String outcome; // GOOD, AVERAGE, POOR

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
