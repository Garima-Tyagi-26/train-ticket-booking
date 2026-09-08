package com.algoworks.trainticketbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algoworks.trainticketbooking.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

	boolean existsByEmail(String email);
}
