package com.algoworks.trainticketbooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.algoworks.trainticketbooking.dto.BookingRequest;
import com.algoworks.trainticketbooking.dto.BookingResponse;
import com.algoworks.trainticketbooking.dto.PassengerRequest;
import com.algoworks.trainticketbooking.dto.PassengerResponse;
import com.algoworks.trainticketbooking.entity.Booking;
import com.algoworks.trainticketbooking.entity.Passenger;
import com.algoworks.trainticketbooking.entity.Train;
import com.algoworks.trainticketbooking.entity.User;
import com.algoworks.trainticketbooking.repository.BookingRepository;
import com.algoworks.trainticketbooking.repository.TrainRepository;
import com.algoworks.trainticketbooking.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class BookingService {

	private final ObjectMapper objectMapper;
	private final MemcachedService memcachedService;
	private final UserRepository userRepository;
	private final TrainRepository trainRepository;
	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository, TrainRepository trainRepository,
			              UserRepository userRepository, MemcachedService memcachedService,  ObjectMapper objectMapper) {
		
		this.bookingRepository = bookingRepository;
		this.trainRepository = trainRepository;
		this.userRepository = userRepository;
		this.memcachedService = memcachedService;
		this.objectMapper = objectMapper; 
	}

	
	@Transactional
	public BookingResponse createBooking(Long userId, BookingRequest request, Authentication authentication) {

	    User loggedInUser = userRepository.findByEmail(authentication.getName())
	                       .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!loggedInUser.getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    Train train = trainRepository.findById(request.getTrainId())
	                .orElseThrow(() -> new RuntimeException("Train not found"));

	    int numberOfPassengers = request.getPassengers().size();

	    int updatedRows = trainRepository.reduceSeatsIfAvailable(request.getTrainId(), numberOfPassengers);

	    if (updatedRows == 0) {
	        throw new RuntimeException("Not enough seats available");
	    }

	    Booking booking = new Booking();

	    booking.setUser(loggedInUser);
	    booking.setTrain(train);
	    booking.setNumberOfSeats(numberOfPassengers);

	    BigDecimal totalFare = train.getFare().multiply(BigDecimal.valueOf(numberOfPassengers));

	    booking.setTotalFare(totalFare);

	    booking.setPnr(
	            UUID.randomUUID()
	                    .toString()
	                    .substring(0, 8)
	                    .toUpperCase()
	    );

	    booking.setStatus("CONFIRMED");
	    booking.setBookingDate(LocalDate.now());

	    List<Passenger> passengers = new ArrayList<>();

	    for (PassengerRequest passengerRequest : request.getPassengers()) {

	        Passenger passenger = new Passenger();

	        passenger.setName(passengerRequest.getName());
	        passenger.setAge(passengerRequest.getAge());
	        passenger.setGender(passengerRequest.getGender());

	        passenger.setBooking(booking);

	        passengers.add(passenger);
	    }

	    booking.setPassengers(passengers);

//	    train.setAvailableSeats(train.getAvailableSeats() - numberOfPassengers); // race condition
//	    trainRepository.save(train);

	    Booking savedBooking = bookingRepository.save(booking);

	    BookingResponse response = new BookingResponse();
	    response.setPnr(savedBooking.getPnr());
	    response.setNumberOfSeats(savedBooking.getNumberOfSeats());
	    response.setTotalFare(savedBooking.getTotalFare());
	    response.setRefundAmount(savedBooking.getRefundAmount());
	    response.setStatus(savedBooking.getStatus());
	    response.setBookingDate(savedBooking.getBookingDate());
	    response.setTrainId(savedBooking.getTrain().getId());

	    List<PassengerResponse> passengerResponses = new ArrayList<>();

	    for (Passenger passenger : savedBooking.getPassengers()) {

	        PassengerResponse passengerResponse = new PassengerResponse();

	        passengerResponse.setName(passenger.getName());
	        passengerResponse.setAge(passenger.getAge());
	        passengerResponse.setGender(passenger.getGender());

	        passengerResponses.add(passengerResponse);
	    }

	    response.setPassengers(passengerResponses);

	    return response;
	}
	
	
	public BookingResponse getBookingByPnr(String pnr) {

	    String key = "booking:" + pnr;

	    // 1. Check mem-cached
	    String cachedBooking = memcachedService.get(key);

	    if (cachedBooking != null) {
	        System.out.println("CACHE HIT: " + key);

	        try {
	            return objectMapper.readValue(cachedBooking, BookingResponse.class);
	        } catch (Exception e) {
	            throw new RuntimeException("Error reading booking from cache", e);
	        }
	    }

	    // 2. Cache MISS -> fetch from database
	    System.out.println("CACHE MISS: " + key);

	    Booking booking = bookingRepository.findByPnr(pnr).orElse(null);

	    if (booking == null) {
	        return null;
	    }

	    // 3. Convert Booking entity -> BookingResponse DTO
	    BookingResponse response = new BookingResponse();

	    response.setPnr(booking.getPnr());
	    response.setNumberOfSeats(booking.getNumberOfSeats());
	    response.setTotalFare(booking.getTotalFare());
	    response.setRefundAmount(booking.getRefundAmount());
	    response.setStatus(booking.getStatus());
	    response.setBookingDate(booking.getBookingDate());
	    response.setTrainId(booking.getTrain().getId());

	    List<PassengerResponse> passengerResponses = new ArrayList<>();

	    for (Passenger passenger : booking.getPassengers()) {

	        PassengerResponse passengerResponse = new PassengerResponse();

	        passengerResponse.setName(passenger.getName());
	        passengerResponse.setAge(passenger.getAge());
	        passengerResponse.setGender(passenger.getGender());

	        passengerResponses.add(passengerResponse);
	    }

	    response.setPassengers(passengerResponses);

	    // 4. Convert DTO -> JSON
	    try {
	        String json = objectMapper.writeValueAsString(response);

	        // 5. Store JSON in Mem-cached for 5 minutes
	        memcachedService.set(key, json, 300);

	    } catch (Exception e) {
	        throw new RuntimeException("Error saving booking to cache", e);
	    }

	    return response;
	}

	
	public List<Booking> getBookingHistory(Long userId, Authentication authentication) {

	    User loggedInUser = userRepository.findByEmail(authentication.getName())
	                        .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!loggedInUser.getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    return bookingRepository.findByUserId(userId);
	}
	
	
	//URL user belongs to logged-in user
	//booking actually belongs to that user
	public Booking getBooking(Long userId, Long bookingId, Authentication authentication) {

	    User loggedInUser = userRepository.findByEmail(authentication.getName())
	                        .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!loggedInUser.getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    Booking booking = bookingRepository.findById(bookingId)
	                     .orElseThrow(() -> new RuntimeException("Booking not found"));

	    if (!booking.getUser().getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    return booking;
	}
	
	
	public Booking cancelBooking(Long userId, Long bookingId, Authentication authentication) {

	    User loggedInUser = userRepository.findByEmail(authentication.getName())
	                       .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!loggedInUser.getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    Booking booking = bookingRepository.findById(bookingId)
	                     .orElseThrow(() -> new RuntimeException("Booking not found"));

	    if (!booking.getUser().getId().equals(userId)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    if ("CANCELLED".equals(booking.getStatus())) {
	        throw new RuntimeException("Booking is already cancelled");
	    }

	    Train train = booking.getTrain();

	    train.setAvailableSeats(train.getAvailableSeats() + booking.getNumberOfSeats());

	    booking.setStatus("CANCELLED");

	    booking.setRefundAmount(booking.getTotalFare());

	    trainRepository.save(train);

	    return bookingRepository.save(booking);
	}
}

