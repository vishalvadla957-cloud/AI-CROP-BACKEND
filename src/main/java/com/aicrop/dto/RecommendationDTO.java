package com.aicrop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    private String primaryCrop;
    private double primaryConfidence;
    private String primaryImageUrl;
    private String primaryDescription;
    private String primaryGrowingTips;

    private List<AlternativeCrop> alternatives;

    private String soilHealthStatus;
    private List<String> soilInsights;
    private List<String> actionableAdvice;
    private Map<String, String> cropCalendar;

    private Long historyId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeCrop {
        private String cropName;
        private double confidence;
        private String reason;
    }
}
