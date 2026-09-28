package com.algoworks.trainticketbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algoworks.trainticketbooking.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

	boolean existsByEmail(String email); //Does a user with this email exist?
	
	Optional<User> findByEmail(String email); //the actual User whose email is this.
	
//	boolean existsByEmail(String email); //Does this email belong to someone other than this user?
	
	Optional<User> findByResetPasswordToken(String token); //user requested for forgot password
}
