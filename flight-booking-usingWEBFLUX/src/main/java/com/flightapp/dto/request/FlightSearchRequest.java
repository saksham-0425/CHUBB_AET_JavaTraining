package com.flightapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequest {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotBlank
    private String date;   

    @Min(1)
    private int seats;

    private String tripType;    
    private String returnDate;   
}
