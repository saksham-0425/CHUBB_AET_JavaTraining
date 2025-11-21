package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    private Long id;

    @Column("pnr")
    private String pnr;

    @Column("user_id")
    private Long userId;

    @Column("flight_id")
    private Long flightId;

    @Column("booking_time")
    private LocalDateTime bookingTime;

    @Column("total_price")
    private BigDecimal totalPrice;

    @Column("status")
    private String status;

    @Column("seats_json")
    private String seatsJson;
}
