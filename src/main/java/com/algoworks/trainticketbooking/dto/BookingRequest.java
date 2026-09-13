package com.algoworks.trainticketbooking.dto;

import java.util.List;

import com.algoworks.trainticketbooking.entity.Passenger;

public class BookingRequest {

	private Long userId;
	private Long trainId;
	private List<Passenger> passengers;
	
	public BookingRequest () {}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTrainId() {
		return trainId;
	}

	public void setTrainId(Long trainId) {
		this.trainId = trainId;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	
	
}
