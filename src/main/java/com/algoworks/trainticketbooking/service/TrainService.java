package com.algoworks.trainticketbooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.algoworks.trainticketbooking.entity.Train;
import com.algoworks.trainticketbooking.repository.TrainRepository;

@Service
public class TrainService {

	private final TrainRepository trainRepository;

	public TrainService(TrainRepository trainRepository) {
		this.trainRepository = trainRepository;
	}
	
	public Train addTrain(Train train) {
		return trainRepository.save(train);
	}
	
	public List<Train> getAllTrains(){
		return trainRepository.findAll();
	}
	
	public Train getTrainById(Long id) {
		return trainRepository.findById(id).orElse(null);
	}
	
	public Train updateTrain(Long id, Train train) {
		 
		Train existingTrain = trainRepository.findById(id).orElse(train);
		
		if(existingTrain == null) {
			return null;
		}
		
		existingTrain.setTrainName(train.getTrainName());
		existingTrain.setTrainNumber(train.getTrainNumber());
		existingTrain.setSource(train.getSource());
	    existingTrain.setDestination(train.getDestination());
	    existingTrain.setDepartureTime(train.getDepartureTime());
	    existingTrain.setArrivalTime(train.getArrivalTime());
	    existingTrain.setTotalSeats(train.getTotalSeats());
	    existingTrain.setAvailableSeats(train.getAvailableSeats());
	    existingTrain.setFare(train.getFare());
		
		return trainRepository.save(existingTrain);
	}
	
	public void deleteTrain(Long id) {
		trainRepository.deleteById(id);
	}
	
	public List<Train> searchTrain(String source, String destination){
		return trainRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
	}
	
}
