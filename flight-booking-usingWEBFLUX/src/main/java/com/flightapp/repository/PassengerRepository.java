package com.flightapp.repository;

import com.flightapp.model.Passenger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends ReactiveCrudRepository<Passenger, Long> {

    Flux<Passenger> findByBookingId(Long bookingId);
}
