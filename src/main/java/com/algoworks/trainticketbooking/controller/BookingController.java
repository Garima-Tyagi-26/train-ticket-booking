package com.algoworks.trainticketbooking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algoworks.trainticketbooking.dto.BookingRequest;
import com.algoworks.trainticketbooking.entity.Booking;
import com.algoworks.trainticketbooking.service.BookingService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}
	
	@PostMapping
	public Booking createBooking(@RequestBody BookingRequest bookingRequest) {
		return bookingService.createBooking(bookingRequest);
	}
	
	@GetMapping("/{pnr}")
	public Booking getBookingByPnr(@PathVariable String pnr) {
	    return bookingService.getBookingByPnr(pnr);
	}

	@GetMapping("/user/{userId}")
	public List<Booking> getBookingHistory(@PathVariable Long userId) {
	    return bookingService.getBookingHistory(userId);
	}
	
	@PutMapping("/{id}/cancel")
	public Booking cancelBooking(@PathVariable Long id) {
	    return bookingService.cancelBooking(id);
	}
}
