package com.algoworks.trainticketbooking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algoworks.trainticketbooking.entity.Train;

public interface TrainRepository extends JpaRepository<Train, Long>{

	List<Train> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndJourneyDate(
	        String source,
	        String destination,
	        LocalDate journeyDate);
}
