package com.algoworks.trainticketbooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algoworks.trainticketbooking.dto.ChangePasswordRequest;
import com.algoworks.trainticketbooking.dto.ForgotPasswordRequest;
import com.algoworks.trainticketbooking.dto.LoginRequest;
import com.algoworks.trainticketbooking.dto.LoginResponse;
import com.algoworks.trainticketbooking.dto.RegisterRequest;
import com.algoworks.trainticketbooking.dto.ResetPasswordRequest;
import com.algoworks.trainticketbooking.dto.UpdateUserRequest;
import com.algoworks.trainticketbooking.dto.UserResponse;
import com.algoworks.trainticketbooking.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	@Operation(
		    summary = "Register a new user",
		    description = "Creates a new user account with name, email, password and phone number."
		)
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "User registered successfully"),
	    @ApiResponse(responseCode = "400", description = "Invalid request data"),
	    @ApiResponse(responseCode = "409", description = "Unable to process registration request")
	})
	@PostMapping
	public UserResponse registerUser(@RequestBody @Valid RegisterRequest request) {
		return userService.registerUser(request);
	}
	
	
	@Operation(
		    summary = "Login user",
		    description = "Authenticates a registered user and returns a JWT token."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Login successful"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data"),
		    @ApiResponse(responseCode = "401", description = "Invalid email or password")
		})
	@PostMapping("/login")
	public LoginResponse loginUser(@RequestBody LoginRequest request) {
	    return userService.loginUser(request);
	}
	
	
	@Operation(
		    summary = "Get my profile",
		    description = "Returns the profile details of the currently authenticated user."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
		    @ApiResponse(responseCode = "401", description = "Authentication required")
		})
	@GetMapping("/me")
	public UserResponse getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return userService.getMyProfile(email);
	}
	
	
	@Operation(
		    summary = "Change my password",
		    description = "Allows the authenticated user to change their password by providing their current password and a new password."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data"),
		    @ApiResponse(responseCode = "401", description = "Authentication required or current password is incorrect")
		})
	@PutMapping("/me/password")
	public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request,
	                                                              Authentication authentication) {

	    userService.changePassword(request, authentication);
        return ResponseEntity.ok("Password changed successfully");
	}
	
	
	@Operation(
		    summary = "Get user profile by ID",
		    description = "Returns a user profile. A user can access only their own profile, while an admin can access user profiles according to the application's access rules."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
		    @ApiResponse(responseCode = "401", description = "Authentication required"),
		    @ApiResponse(responseCode = "403", description = "Access denied"),
		    @ApiResponse(responseCode = "404", description = "User not found")
		})
	@GetMapping("/{id}")
	public UserResponse getUserById(@PathVariable Long id, Authentication authentication) {
        return userService.getUserById(id, authentication);
	}
	
	
	@Operation(
		    summary = "Update my profile",
		    description = "Updates the authenticated user's name, email, or phone number. Users can update only their own profile."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data"),
		    @ApiResponse(responseCode = "401", description = "Authentication required"),
		    @ApiResponse(responseCode = "403", description = "Access denied"),
		    @ApiResponse(responseCode = "409", description = "Unable to process update request")
		})
	@PutMapping("/me")
	public UserResponse updateUser(@RequestBody  @Valid UpdateUserRequest request, 
			                                                    Authentication authentication) {

	    return userService.updateUser(request, authentication);
	}
	
	
	@Operation(
		    summary = "Request password reset",
		    description = "Generates a password reset token for the provided email address."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Password reset request processed successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid email address")
		})
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {

	    userService.forgotPassword(request);
        return ResponseEntity.ok(
	            "If the email is registered, password reset instructions have been sent."
	    );
	}
	
	
	@Operation(
		    summary = "Reset password",
		    description = "Resets the user's password using a valid and non-expired password reset token."
		)
		@ApiResponses({
		    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid request data"),
		    @ApiResponse(responseCode = "500", description = "Invalid or expired reset token")
		})
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
	}
	
}
