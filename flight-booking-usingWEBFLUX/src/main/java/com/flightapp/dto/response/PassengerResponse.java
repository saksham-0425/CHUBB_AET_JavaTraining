package com.flightapp.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponse {

    private String name;
    private String gender;
    private Integer age;
    private String mealPref;
    private String seatNumber;
}
