package com.aicrop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiQueryDTO {
    @NotBlank(message = "Query cannot be empty")
    private String query;
    private String context; // optional: current crop or context
    private String mode;    // "short" or "detailed"
}
