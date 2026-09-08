package com.algoworks.trainticketbooking.service;

import org.springframework.stereotype.Service;

import com.algoworks.trainticketbooking.dto.RegisterRequest;
import com.algoworks.trainticketbooking.entity.Role;
import com.algoworks.trainticketbooking.entity.User;
import com.algoworks.trainticketbooking.exception.EmailAlreadyExistsException;
import com.algoworks.trainticketbooking.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User registerUser(RegisterRequest request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email Already Exists");
		}
			
		User user = new User(); 
		
		user.setName(request.getEmail());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setPhone(request.getPhone());
		user.setRole(Role.USER);
		
		return userRepository.save(user);
	}
	
}
