package com.flightapp.controller;

import com.flightapp.dto.request.BookingRequest;
import com.flightapp.dto.response.BookingResponse;
import com.flightapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public Mono<BookingResponse> bookTicket(
            @PathVariable Long flightId,
            @Valid @RequestBody BookingRequest request) {

        return bookingService.bookTicket(flightId, request, request.getEmail());
    }

    @GetMapping("/ticket/{pnr}")
    public Mono<BookingResponse> getTicket(@PathVariable String pnr) {
        return bookingService.getTicketByPnr(pnr);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<String> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .thenReturn("Booking cancelled successfully");
    }
}
