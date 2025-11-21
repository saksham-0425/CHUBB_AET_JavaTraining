package com.flightapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequest {

    @NotBlank
    private String name;

    private Integer age;

    private String gender;

    private String mealPref;

    private String seatNumber;
}
