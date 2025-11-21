package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.NotBlank;

@Table("airline")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline {

    @Id
    private Long id;

    @NotBlank(message = "Airline name cannot be empty")
    @Column("name")
    private String name;

    @Column("code")
    private String code;

    @Column("logo_url")
    private String logoUrl;
}
