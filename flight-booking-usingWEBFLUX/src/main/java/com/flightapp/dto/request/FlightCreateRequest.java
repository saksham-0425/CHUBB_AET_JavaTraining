package com.flightapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightCreateRequest {

    @NotNull
    private Long airlineId;

    @NotBlank
    private String flightNumber;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private LocalDateTime departDatetime;

    @NotNull
    private LocalDateTime arriveDatetime;

    @NotNull
    private Integer durationMin;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer totalSeats;
}
