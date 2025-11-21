package com.flightapp.service;

import com.flightapp.dto.request.BookingRequest;
import com.flightapp.dto.response.BookingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Mono<BookingResponse> bookTicket(Long flightId, BookingRequest request, String userEmail);

    Mono<BookingResponse> getTicketByPnr(String pnr);

    Flux<BookingResponse> getBookingHistory(String email);

    Mono<Void> cancelBooking(String pnr);
}
