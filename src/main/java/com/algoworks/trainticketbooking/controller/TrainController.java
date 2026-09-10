package com.algoworks.trainticketbooking.controller;

import com.algoworks.trainticketbooking.service.UserService;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algoworks.trainticketbooking.entity.Train;
import com.algoworks.trainticketbooking.service.TrainService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/trains")
public class TrainController {

	private final UserService userService;
	private final TrainService trainService;

	public TrainController(TrainService trainService, UserService userService) {
		this.trainService = trainService;
		this.userService = userService;
	}
	
	@PostMapping
	public Train addTrain(@RequestBody Train train) {
		return trainService.addTrain(train);
	}
	
	@GetMapping
	public List<Train> getAllTrains() {
		return trainService.getAllTrains();
	}
	
	@GetMapping("/{id}")
	public Train getTrainById(@PathVariable Long id) {
		return trainService.getTrainById(id);
	}
	
	@PutMapping("/{id}")
	public Train updateTrain(@PathVariable Long id, @RequestBody Train train) {
		return trainService.updateTrain(id, train);
	}
	
	@DeleteMapping("/{id}")
	public void deleteTrain(@PathVariable Long id) {
		trainService.deleteTrain(id);
	}
	
	@GetMapping("/search")
	public List<Train> searchTrain(@RequestParam String source, @RequestParam String destination) {
		return trainService.searchTrain(source, destination);
	}
	
}
