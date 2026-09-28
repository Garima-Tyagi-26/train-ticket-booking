package com.algoworks.trainticketbooking.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.algoworks.trainticketbooking.dto.ChangePasswordRequest;
import com.algoworks.trainticketbooking.dto.ForgotPasswordRequest;
import com.algoworks.trainticketbooking.dto.LoginRequest;
import com.algoworks.trainticketbooking.dto.LoginResponse;
import com.algoworks.trainticketbooking.dto.RegisterRequest;
import com.algoworks.trainticketbooking.dto.ResetPasswordRequest;
import com.algoworks.trainticketbooking.dto.UpdateUserRequest;
import com.algoworks.trainticketbooking.dto.UserResponse;
import com.algoworks.trainticketbooking.entity.Role;
import com.algoworks.trainticketbooking.entity.User;
import com.algoworks.trainticketbooking.exception.EmailAlreadyExistsException;
import com.algoworks.trainticketbooking.exception.InvalidPasswordException;
import com.algoworks.trainticketbooking.repository.UserRepository;

@Service
public class UserService {

	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	// method for user registration
	public UserResponse registerUser(RegisterRequest request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(  "Unable to process registration request");
		}
			
		User user = new User(); 
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhone(request.getPhone());
		user.setRole(Role.USER);
		
		User savedUser = userRepository.save(user);
		
		UserResponse response = new UserResponse();
		response.setId(savedUser.getId());
		response.setName(savedUser.getName());
		response.setEmail(savedUser.getEmail());
		response.setPhone(savedUser.getPhone());
		
		return response;
	}
	
	// method for user login
	public LoginResponse loginUser(LoginRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

	    if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
	    }
	    
	    String token = jwtService.generateToken(user.getEmail(), user.getRole().name()); //we're putting the role inside the JWT
	    
	    return new LoginResponse(
	            user.getId(),
	            user.getName(),
	            user.getEmail(),
	            token
	    );
	}
	
	// method to get the user details with id
	public UserResponse getUserById(Long id, Authentication authentication) {

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    String loggedInEmail = authentication.getName();

	    boolean isAdmin = authentication.getAuthorities().stream()
	            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

	    if (!isAdmin && !user.getEmail().equals(loggedInEmail)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    UserResponse response = new UserResponse();

	    response.setId(user.getId());
	    response.setName(user.getName());
	    response.setEmail(user.getEmail());
	    response.setPhone(user.getPhone());
	   // response.setRole(user.getRole());

	    return response;
	}
	
	// method for the user to update their details
	public UserResponse updateUser(UpdateUserRequest request, Authentication authentication) {

		   String loggedInEmail = authentication.getName();

		    User user = userRepository.findByEmail(loggedInEmail)
		            .orElseThrow(() -> new RuntimeException("User not found"));

	    // User can update only their own profile
	    if (!user.getEmail().equals(loggedInEmail)) {
	        throw new AccessDeniedException("Access denied");
	    }

	    // Check if the new email is already used by another user
	    if (request.getEmail() != null
	            && userRepository.existsByEmail(request.getEmail())) {

	        throw new EmailAlreadyExistsException(
	                "Unable to process update request");
	    }

	    // Update only the fields provided in the request
	    if (request.getName() != null) {
	        user.setName(request.getName());
	    }

	    if (request.getEmail() != null) {
	        user.setEmail(request.getEmail());
	    }

	    if (request.getPhone() != null) {
	        user.setPhone(request.getPhone());
	    }

	    // Save updated user
	    User updatedUser = userRepository.save(user);

	    UserResponse response = new UserResponse();
	    response.setId(updatedUser.getId());
	    response.setName(updatedUser.getName());
	    response.setEmail(updatedUser.getEmail());
	    response.setPhone(updatedUser.getPhone());
	   // response.setRole(updatedUser.getRole());

	    return response;
	}
	
	//method for access their own profile
	public UserResponse getMyProfile(String email) {

	    User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	    UserResponse response = new UserResponse();

	    response.setId(user.getId());
	    response.setName(user.getName());
	    response.setEmail(user.getEmail());
	    response.setPhone(user.getPhone());

	    return response;
	}
	
	//method to change current password for logged-in user
	public void changePassword(ChangePasswordRequest request, Authentication authentication) {

	    String loggedInEmail = authentication.getName();

	    User user = userRepository.findByEmail(loggedInEmail)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    // Check current password
	    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
	    	 throw new InvalidPasswordException("Current password is incorrect");
	    }

	    // Encode and save the new password
	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

	    userRepository.save(user);
	}
	
	//method to process the forgot password request
	public void forgotPassword(ForgotPasswordRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
	}
	
	//reset the password for the request made by the user as forgot password
	public void resetPassword(ResetPasswordRequest request) {

	    User user = userRepository.findByResetPasswordToken(request.getToken())
	            .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

	    if (user.getResetPasswordTokenExpiry() == null ||
	        user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {

	        throw new RuntimeException("Invalid or expired token");
	    }

	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));

	    // Token can be used only once
	    user.setResetPasswordToken(null);
	    user.setResetPasswordTokenExpiry(null);

	    userRepository.save(user);
	}
}
