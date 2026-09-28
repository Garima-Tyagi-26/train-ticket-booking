package com.algoworks.trainticketbooking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algoworks.trainticketbooking.dto.BookingRequest;
import com.algoworks.trainticketbooking.dto.BookingResponse;
import com.algoworks.trainticketbooking.entity.Booking;
import com.algoworks.trainticketbooking.service.BookingService;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}
	
	@PostMapping("/{userId}/bookings")
	public BookingResponse createBooking(@PathVariable Long userId, @RequestBody BookingRequest bookingRequest,
                                                            Authentication authentication) {
		return bookingService.createBooking(userId, bookingRequest, authentication);
	}
	
	@GetMapping("/bookings/pnr/{pnr}")
	public BookingResponse getBookingByPnr(@PathVariable String pnr) {
	    return bookingService.getBookingByPnr(pnr);
	}
	 
	@GetMapping("/{userId}/bookings")
	public List<Booking> getBookingHistory(@PathVariable Long userId, Authentication authentication) {
	    return bookingService.getBookingHistory(userId, authentication);
	}
	
	@GetMapping("/{userId}/bookings/{bookingId}")
	public Booking getBooking(@PathVariable Long userId, @PathVariable Long bookingId, Authentication authentication) {
        return bookingService.getBooking(userId, bookingId, authentication);
	}
	 
	@PutMapping("/{userId}/bookings/{bookingId}/cancel")
	public Booking cancelBooking(@PathVariable Long userId, @PathVariable Long bookingId, Authentication authentication) {
        return bookingService.cancelBooking(userId, bookingId, authentication);
	}

}
