package com.algoworks.trainticketbooking.service;

import org.springframework.stereotype.Service;

import com.algoworks.trainticketbooking.dto.LoginRequest;
import com.algoworks.trainticketbooking.dto.LoginResponse;
import com.algoworks.trainticketbooking.dto.RegisterRequest;
import com.algoworks.trainticketbooking.dto.UserResponse;
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
	
	public UserResponse registerUser(RegisterRequest request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email Already Exists");
		}
			
		User user = new User(); 
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setPhone(request.getPhone());
		user.setRole(Role.USER);
		
		User savedUser = userRepository.save(user);
		
		UserResponse response = new UserResponse();
		response.setId(savedUser.getId());
		response.setName(savedUser.getName());
		response.setEmail(savedUser.getEmail());
		response.setPhone(savedUser.getPhone());
		response.setRole(savedUser.getRole());
		
		return response;
	}
	
	public LoginResponse loginUser(LoginRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

	    if (!user.getPassword().equals(request.getPassword())) {
	        throw new RuntimeException("Invalid email or password");
	    }

	    return new LoginResponse(
	            user.getId(),
	            user.getName(),
	            user.getEmail(),
	            user.getRole().name()
	    );
	}
	
}
