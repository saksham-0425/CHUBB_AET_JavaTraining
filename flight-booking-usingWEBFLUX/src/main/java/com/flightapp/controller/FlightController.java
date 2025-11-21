package com.flightapp.controller;

import com.flightapp.dto.request.FlightCreateRequest;
import com.flightapp.dto.request.FlightSearchRequest;
import com.flightapp.dto.response.FlightSearchResponse;
import com.flightapp.model.Flight;
import com.flightapp.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public Mono<Flight> addInventory(@Valid @RequestBody FlightCreateRequest request) {
        return flightService.addInventory(request);
    }

    @PostMapping("/search")
    public Flux<FlightSearchResponse> searchFlights(
            @Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }
}
