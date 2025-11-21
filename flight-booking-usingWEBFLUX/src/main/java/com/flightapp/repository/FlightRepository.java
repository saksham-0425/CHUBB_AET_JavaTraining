package com.flightapp.repository;

import com.flightapp.model.Flight;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends ReactiveCrudRepository<Flight, Long> {

    @Query("""
        SELECT * FROM flight 
        WHERE origin = :origin 
          AND destination = :destination
          AND depart_datetime BETWEEN :start AND :end
        """)
    Flux<Flight> searchFlights(String origin,
                               String destination,
                               LocalDateTime start,
                               LocalDateTime end);
}
