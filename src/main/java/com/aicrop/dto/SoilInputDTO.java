package com.aicrop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SoilInputDTO {

    @NotNull(message = "Nitrogen value is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "300.0")
    private Double nitrogen;

    @NotNull(message = "Phosphorus value is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "300.0")
    private Double phosphorus;

    @NotNull(message = "Potassium value is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "300.0")
    private Double potassium;

    @NotNull(message = "pH value is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "14.0")
    private Double ph;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-10.0") @DecimalMax(value = "60.0")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private Double humidity;

    @NotNull(message = "Rainfall is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "500.0")
    private Double rainfall;

    private String season;
    private String notes;
}
