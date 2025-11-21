package com.flightapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @Min(1)
    private Integer numSeats;

    @NotEmpty
    private List<PassengerRequest> passengers;
}
