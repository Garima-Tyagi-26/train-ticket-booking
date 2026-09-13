package com.algoworks.trainticketbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.algoworks.trainticketbooking.dto.BookingRequest;
import com.algoworks.trainticketbooking.entity.Booking;
import com.algoworks.trainticketbooking.entity.Passenger;
import com.algoworks.trainticketbooking.entity.Train;
import com.algoworks.trainticketbooking.entity.User;
import com.algoworks.trainticketbooking.repository.BookingRepository;
import com.algoworks.trainticketbooking.repository.TrainRepository;
import com.algoworks.trainticketbooking.repository.UserRepository;

@Service
public class BookingService {

	private final UserRepository userRepository;
	private final TrainRepository trainRepository;
	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository,
			                                UserRepository userRepository) {
		
		this.bookingRepository = bookingRepository;
		this.trainRepository = trainRepository;
		this.userRepository = userRepository;
	}

	public Booking createBooking(BookingRequest request) {             //create Booking method
		
		//System.out.println("User ID: " + request.getUserId());
		//System.out.println("Train ID: " + request.getTrainId());
		
		User user = userRepository.findById(request.getUserId())
				    .orElseThrow(() -> new RuntimeException("User not found"));
		
		Train train = trainRepository.findById(request.getTrainId())
				      .orElseThrow(() -> new RuntimeException("Train not found"));
		
		int numberOfPassengers = request.getPassengers().size();
		
		if(train.getAvailableSeats() < numberOfPassengers) {
			throw new RuntimeException("Not enough seats available");
	    }
		
		Booking booking = new Booking();
		booking.setUser(user);
		booking.setTrain(train);
		booking.setNumberOfSeats(numberOfPassengers);
		
		BigDecimal totalFare = train.getFare().multiply(BigDecimal.valueOf(numberOfPassengers));
		booking.setTotalFare(totalFare);
		
		booking.setPnr(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		booking.setStatus("CONFIRMED");
	    booking.setBookingDate(LocalDate.now());
	    
	    List<Passenger> passengers = request.getPassengers();
	    for (Passenger passenger : passengers) {
	        passenger.setBooking(booking);
	    }

	    booking.setPassengers(passengers);
	    
	    train.setAvailableSeats(train.getAvailableSeats() - numberOfPassengers);
	    trainRepository.save(train);
	    

	    return bookingRepository.save(booking);
		
	}
	
	
	public Booking getBookingByPnr(String pnr) {              //method for searching booking
	    return bookingRepository.findByPnr(pnr)
	            .orElse(null);
	}

	
	public List<Booking> getBookingHistory(Long userId) {     //method for searching all booking of user
	    return bookingRepository.findByUserId(userId);
	}
	
	
	public Booking cancelBooking(Long id) {                   //Cancel the particular booking

	    Booking booking = bookingRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Booking not found"));

	    if ("CANCELLED".equals(booking.getStatus())) {
	        throw new RuntimeException("Booking is already cancelled");
	    }

	    Train train = booking.getTrain();

	    train.setAvailableSeats(
	            train.getAvailableSeats() + booking.getNumberOfSeats()
	    );

	    booking.setStatus("CANCELLED");

	    // 100% refund
	    booking.setRefundAmount(booking.getTotalFare());

	    trainRepository.save(train);

	    return bookingRepository.save(booking);
	}
}

