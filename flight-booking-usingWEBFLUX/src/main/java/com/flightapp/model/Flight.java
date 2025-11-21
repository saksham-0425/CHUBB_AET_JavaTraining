package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    private Long id;

    @Column("airline_id")
    private Long airlineId;

    @NotBlank
    @Column("flight_number")
    private String flightNumber;

    @NotBlank
    @Column("origin")
    private String origin;

    @NotBlank
    @Column("destination")
    private String destination;

    @NotNull
    @Column("depart_datetime")
    private LocalDateTime departDatetime;

    @NotNull
    @Column("arrive_datetime")
    private LocalDateTime arriveDatetime;

    @NotNull
    @Column("duration_min")
    private Integer durationMin;

    @NotNull
    @Column("price")
    private BigDecimal price;

    @NotNull
    @Column("total_seats")
    private Integer totalSeats;

    @NotNull
    @Column("available_seats")
    private Integer availableSeats;
}
