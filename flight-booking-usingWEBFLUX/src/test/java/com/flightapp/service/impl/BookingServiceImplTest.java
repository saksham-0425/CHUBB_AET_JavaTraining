package com.flightapp.service.impl;

import com.flightapp.dto.request.BookingRequest;
import com.flightapp.dto.request.PassengerRequest;
import com.flightapp.dto.response.BookingResponse;
import com.flightapp.model.*;
import com.flightapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private TransactionalOperator tx;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private AppUser user;
    private Flight flight;
    private Airline airline;

    @BeforeEach
    void setUp() {
        // MockitoExtension handles initialization
        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setName("Test User");

        airline = new Airline();
        airline.setId(5L);
        airline.setName("AirTest");

        flight = new Flight();
        flight.setId(10L);
        flight.setAirlineId(airline.getId());
        flight.setFlightNumber("AI-202");
        flight.setOrigin("DEL");
        flight.setDestination("MUM");
        flight.setPrice(BigDecimal.valueOf(5000));
        flight.setAvailableSeats(10);
        flight.setDepartDatetime(LocalDateTime.now().plusDays(2));

        // make TransactionalOperator just pass through the publisher (identity) for tests
        // Mockito needs a stubbing that returns the given publisher; use thenAnswer
        when(tx.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));


    }

    private BookingRequest sampleRequest() {
        PassengerRequest p = new PassengerRequest();
        p.setName("John");
        p.setAge(25);
        p.setGender("M");

        BookingRequest req = new BookingRequest();
        req.setNumSeats(2);
        req.setPassengers(List.of(p));
        req.setEmail(user.getEmail());
        req.setName(user.getName());

        return req;
    }

    @Test
    void testBookTicket_Success() {
        BookingRequest request = sampleRequest();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(user));

        when(flightRepository.findById(anyLong()))
                .thenReturn(Mono.just(flight));

        Booking savedBooking = new Booking();
        savedBooking.setId(99L);
        savedBooking.setUserId(user.getId());
        savedBooking.setFlightId(flight.getId());
        savedBooking.setPnr("PNR123");
        savedBooking.setTotalPrice(flight.getPrice().multiply(BigDecimal.valueOf(request.getNumSeats())));
        savedBooking.setStatus("CONFIRMED");
        savedBooking.setBookingTime(LocalDateTime.now());

        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(savedBooking));

        // passengerRepository.saveAll returns Flux of saved passengers
        Passenger createdPassenger = new Passenger();
        createdPassenger.setId(201L);
        createdPassenger.setBookingId(savedBooking.getId());
        createdPassenger.setName("John");

        when(passengerRepository.saveAll(anyIterable())).thenReturn(Flux.just(createdPassenger));

        // flightRepository.save called to update seats
        Flight updatedFlight = new Flight();
        updatedFlight.setId(flight.getId());
        updatedFlight.setAvailableSeats(flight.getAvailableSeats() - request.getNumSeats());
        when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(updatedFlight));

        // For buildBookingResponse: bookingRepository.findByPnr is not used here since we use returned savedBooking directly,
        // but buildBookingResponse will call userRepository.findById and flightRepository.findById and passengerRepository.findByBookingId
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(flightRepository.findById(flight.getId())).thenReturn(Mono.just(flight));
        when(passengerRepository.findByBookingId(savedBooking.getId())).thenReturn(Flux.just(createdPassenger));

        Mono<BookingResponse> result = bookingService.bookTicket(flight.getId(), request, user.getEmail());

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getPnr().equals("PNR123");
                    assert resp.getUserEmail().equals(user.getEmail());
                    assert resp.getFlightId().equals(flight.getId());
                    assert resp.getPassengers() != null && resp.getPassengers().size() == 1;
                })
                .verifyComplete();

        verify(bookingRepository, times(1)).save(any());
        verify(passengerRepository, times(1)).saveAll(anyIterable());
        verify(flightRepository, times(1)).save(any());
    }

    @Test
    void testBookTicket_UserNotFound() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.bookTicket(10L, sampleRequest(), "invalid@mail.com"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("User not found"))
                .verify();
    }

    @Test
    void testBookTicket_FlightNotFound() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(user));

        when(flightRepository.findById(anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.bookTicket(10L, sampleRequest(), user.getEmail()))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Flight not found"))
                .verify();
    }

    @Test
    void testBookTicket_InsufficientSeats() {
        flight.setAvailableSeats(1);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(user));

        when(flightRepository.findById(anyLong()))
                .thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.bookTicket(10L, sampleRequest(), user.getEmail()))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Not enough seats available"))
                .verify();
    }

    @Test
    void testGetTicketByPnr_Success() {
        Booking booking = new Booking();
        booking.setId(50L);
        booking.setPnr("PNR123");
        booking.setUserId(user.getId());
        booking.setFlightId(flight.getId());

        when(bookingRepository.findByPnr("PNR123"))
                .thenReturn(Mono.just(booking));

        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(flightRepository.findById(flight.getId())).thenReturn(Mono.just(flight));
        Passenger p = new Passenger();
        p.setName("John");
        when(passengerRepository.findByBookingId(booking.getId())).thenReturn(Flux.just(p));

        StepVerifier.create(bookingService.getTicketByPnr("PNR123"))
                .assertNext(resp -> {
                    assert resp.getPnr().equals("PNR123");
                    assert resp.getUserEmail().equals(user.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void testGetTicketByPnr_NotFound() {
        when(bookingRepository.findByPnr(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getTicketByPnr("INVALID"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("PNR not found"))
                .verify();
    }

    @Test
    void testGetBookingHistory() {
        Booking b = new Booking();
        b.setId(111L);
        b.setUserId(user.getId());
        b.setFlightId(flight.getId());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(bookingRepository.findByUserId(user.getId())).thenReturn(Flux.just(b));
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(flightRepository.findById(flight.getId())).thenReturn(Mono.just(flight));
        when(passengerRepository.findByBookingId(b.getId())).thenReturn(Flux.just(new Passenger()));

        StepVerifier.create(bookingService.getBookingHistory(user.getEmail()).collectList())
        .assertNext(list -> assertEquals(1, list.size()))
                .verifyComplete();
    }

    @Test
    void testCancelBooking_Success() {
        Booking booking = new Booking();
        booking.setId(200L);
        booking.setFlightId(flight.getId());

        when(bookingRepository.findByPnr("PNR123"))
                .thenReturn(Mono.just(booking));

        when(flightRepository.findById(flight.getId()))
                .thenReturn(Mono.just(flight));

        Passenger p = new Passenger();
        when(passengerRepository.findByBookingId(booking.getId())).thenReturn(Flux.just(p));

        when(bookingRepository.save(any(Booking.class))).thenReturn(Mono.just(booking));
        when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.cancelBooking("PNR123"))
                .verifyComplete();

        verify(bookingRepository, times(1)).save(any());
        verify(flightRepository, times(1)).save(any());
    }

    @Test
    void testCancelBooking_NotFound() {
        when(bookingRepository.findByPnr(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelBooking("NOPE"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("PNR not found"))
                .verify();
    }

    @Test
    void testCancelBooking_TooLate() {
        flight.setDepartDatetime(LocalDateTime.now().plusHours(5)); // <24 hours

        Booking booking = new Booking();
        booking.setId(300L);
        booking.setFlightId(flight.getId());

        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(booking));
        when(flightRepository.findById(flight.getId())).thenReturn(Mono.just(flight));

        StepVerifier.create(bookingService.cancelBooking("PNR123"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Cannot cancel within 24 hours"))
                .verify();
    }
}
