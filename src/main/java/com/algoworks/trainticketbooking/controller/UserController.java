package com.algoworks.trainticketbooking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algoworks.trainticketbooking.dto.RegisterRequest;
import com.algoworks.trainticketbooking.dto.UserResponse;
import com.algoworks.trainticketbooking.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping
	public UserResponse registerUser(@RequestBody @Valid RegisterRequest request) {
		return userService.registerUser(request);
	}
}
