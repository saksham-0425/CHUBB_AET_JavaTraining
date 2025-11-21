package com.flightapp.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private String pnr;

    private String userName;
    private String userEmail;

    private Long flightId;
    private String flightNumber;
    private String airlineName;

    private String origin;
    private String destination;
    private LocalDateTime departDatetime;

    private BigDecimal totalPrice;

    private String status;

    private List<PassengerResponse> passengers;
}
