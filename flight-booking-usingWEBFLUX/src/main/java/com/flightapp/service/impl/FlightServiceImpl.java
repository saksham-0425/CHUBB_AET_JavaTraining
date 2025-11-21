package com.flightapp.service.impl;

import com.flightapp.dto.request.FlightCreateRequest;
import com.flightapp.dto.request.FlightSearchRequest;
import com.flightapp.dto.response.FlightSearchResponse;
import com.flightapp.model.Airline;
import com.flightapp.model.Flight;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    @Override
    public Mono<Flight> addInventory(FlightCreateRequest req) {
        return airlineRepository.findById(req.getAirlineId())
                .switchIfEmpty(Mono.error(new RuntimeException("Airline not found")))
                .flatMap(airline -> {
                    Flight flight = Flight.builder()
                            .airlineId(airline.getId())
                            .flightNumber(req.getFlightNumber())
                            .origin(req.getOrigin())
                            .destination(req.getDestination())
                            .departDatetime(req.getDepartDatetime())
                            .arriveDatetime(req.getArriveDatetime())
                            .durationMin(req.getDurationMin())
                            .price(req.getPrice())
                            .totalSeats(req.getTotalSeats())
                            .availableSeats(req.getTotalSeats())
                            .build();

                    return flightRepository.save(flight);
                });
    }

    @Override
    public Flux<FlightSearchResponse> searchFlights(FlightSearchRequest req) {
        LocalDate date = LocalDate.parse(req.getDate());
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return flightRepository.searchFlights(req.getOrigin(), req.getDestination(), start, end)
                .flatMap(flight ->
                    airlineRepository.findById(flight.getAirlineId())
                            .map(airline -> FlightSearchResponse.builder()
                                    .flightId(flight.getId())
                                    .airlineName(airline.getName())
                                    .flightNumber(flight.getFlightNumber())
                                    .origin(flight.getOrigin())
                                    .destination(flight.getDestination())
                                    .departDatetime(flight.getDepartDatetime())
                                    .arriveDatetime(flight.getArriveDatetime())
                                    .durationMin(flight.getDurationMin())
                                    .price(flight.getPrice())
                                    .availableSeats(flight.getAvailableSeats())
                                    .build()
                            )
                );
    }

    @Override
    public Mono<Flight> getFlightById(Long id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")));
    }
}
