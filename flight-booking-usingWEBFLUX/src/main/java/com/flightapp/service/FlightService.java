package com.flightapp.service;

import com.flightapp.dto.request.FlightCreateRequest;
import com.flightapp.dto.request.FlightSearchRequest;
import com.flightapp.dto.response.FlightSearchResponse;
import com.flightapp.model.Flight;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {

    Mono<Flight> addInventory(FlightCreateRequest request);

    Flux<FlightSearchResponse> searchFlights(FlightSearchRequest request);

    Mono<Flight> getFlightById(Long id);
}
