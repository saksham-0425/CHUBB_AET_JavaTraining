package com.flightapp.service.impl;


import com.flightapp.dto.request.BookingRequest;
import com.flightapp.dto.request.PassengerRequest;
import com.flightapp.dto.response.BookingResponse;
import com.flightapp.dto.response.PassengerResponse;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.Passenger;
import com.flightapp.model.AppUser;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.repository.AppUserRepository;
import com.flightapp.service.BookingService;
import com.flightapp.util.PnrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final AppUserRepository userRepository;
    private final FlightRepository flightRepository;
    private final TransactionalOperator tx; 
    @Override
    public Mono<BookingResponse> bookTicket(Long flightId, BookingRequest request, String userEmail) {
       
        return userRepository.findByEmail(userEmail)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user ->
                    flightRepository.findById(flightId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
                        .flatMap(flight -> {
                            if (flight.getAvailableSeats() < request.getNumSeats()) {
                                return Mono.error(new RuntimeException("Not enough seats available"));
                            }

                            Booking booking = new Booking();
                            booking.setFlightId(flight.getId());
                            booking.setUserId(user.getId());
                            booking.setStatus("CONFIRMED");
                            booking.setBookingTime(LocalDateTime.now());
                            booking.setPnr(PnrGenerator.generate());
                            booking.setTotalPrice(flight.getPrice().multiply(BigDecimal.valueOf(request.getNumSeats())));
                            booking.setSeatsJson("NA");

                          
                            return bookingRepository.save(booking)
                                .flatMap(savedBooking -> {
                                
                                    List<Passenger> passengers = request.getPassengers().stream().map(pr -> {
                                        Passenger p = new Passenger();
                                        p.setBookingId(savedBooking.getId());
                                        p.setName(pr.getName());
                                        p.setAge(pr.getAge());
                                        p.setGender(pr.getGender());
                                        p.setMealPref(pr.getMealPref());
                                        p.setSeatNumber(pr.getSeatNumber() == null ? "AUTO" : pr.getSeatNumber());
                                        return p;
                                    }).collect(Collectors.toList());

                                    
                                    Flux<Passenger> savedPassengersFlux = passengerRepository.saveAll(passengers);

                               
                                    flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumSeats());
                                    Mono<Flight> updatedFlightMono = flightRepository.save(flight);

                                   
                                    return savedPassengersFlux.collectList()
                                            .then(updatedFlightMono)
                                            .then(buildBookingResponse(savedBooking));
                                })
                                .as(tx::transactional); 
                        })
                );
    }

    @Override
    public Mono<BookingResponse> getTicketByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new RuntimeException("PNR not found")))
                .flatMap(this::buildBookingResponse);
    }

    @Override
    public Flux<BookingResponse> getBookingHistory(String email) {
        // find user by email -> find bookings by userId -> map to responses
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMapMany(user -> bookingRepository.findByUserId(user.getId()))
                .flatMap(this::buildBookingResponse);
    }

    @Override
    public Mono<Void> cancelBooking(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new RuntimeException("PNR not found")))
                .flatMap(booking ->
                    flightRepository.findById(booking.getFlightId())
                        .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
                        .flatMap(flight -> {
                            if (LocalDateTime.now().isAfter(flight.getDepartDatetime().minusHours(24))) {
                                return Mono.error(new RuntimeException("Cannot cancel within 24 hours of departure"));
                            }

                            booking.setStatus("CANCELLED");

                      
                            return passengerRepository.findByBookingId(booking.getId()).collectList()
                                    .flatMap(passengers -> {
                                        int seatCount = passengers.size();
                                        flight.setAvailableSeats(flight.getAvailableSeats() + seatCount);

                                        Mono<Booking> saveBooking = bookingRepository.save(booking);
                                        Mono<Flight> saveFlight = flightRepository.save(flight);

                                       
                                        return Mono.zip(saveBooking, saveFlight)
                                                .then()
                                                .as(tx::transactional);
                                    });
                        })
                )
                .then(); 
    }

   
    private Mono<BookingResponse> buildBookingResponse(Booking booking) {
        Mono<AppUser> userMono = userRepository.findById(booking.getUserId());
        Mono<Flight> flightMono = flightRepository.findById(booking.getFlightId());
        Flux<com.flightapp.model.Passenger> passengersFlux = passengerRepository.findByBookingId(booking.getId());

        return Mono.zip(
                userMono,
                flightMono,
                passengersFlux.collectList()
        ).flatMap(tuple -> {

            AppUser user = tuple.getT1();
            Flight flight = tuple.getT2();
            List<Passenger> passengers = tuple.getT3();

            BookingResponse response = BookingResponse.builder()
                    .pnr(booking.getPnr())
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .flightId(flight.getId())
                    .flightNumber(flight.getFlightNumber())
                    .airlineName(null) 
                    .origin(flight.getOrigin())
                    .destination(flight.getDestination())
                    .departDatetime(flight.getDepartDatetime())
                    .totalPrice(booking.getTotalPrice())
                    .status(booking.getStatus())
                    .passengers(
                            passengers.stream()
                                    .map(p -> PassengerResponse.builder()
                                            .name(p.getName())
                                            .gender(p.getGender())
                                            .age(p.getAge())
                                            .mealPref(p.getMealPref())
                                            .seatNumber(p.getSeatNumber())
                                            .build()
                                    )
                                    .toList()
                    )
                    .build();

            return Mono.just(response);
        });

    }
}
