package com.flightapp.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResponse {

    private Long flightId;

    private String airlineName;
    private String flightNumber;

    private String origin;
    private String destination;

    private LocalDateTime departDatetime;
    private LocalDateTime arriveDatetime;

    private Integer durationMin;

    private BigDecimal price;

    private Integer availableSeats;
}
