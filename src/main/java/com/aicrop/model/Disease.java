package com.aicrop.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "diseases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String affectedCrop;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String causes;

    @Column(columnDefinition = "TEXT")
    private String remedialMeasures;

    @Column(columnDefinition = "TEXT")
    private String preventiveMeasures;

    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    private String category; // FUNGAL, BACTERIAL, VIRAL, PEST, NUTRIENT_DEFICIENCY
}
