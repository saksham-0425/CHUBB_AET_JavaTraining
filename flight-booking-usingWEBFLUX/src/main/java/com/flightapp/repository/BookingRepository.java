package com.flightapp.repository;

import com.flightapp.model.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {

    Mono<Booking> findByPnr(String pnr);

    Flux<Booking> findByUserId(Long userId);
}
