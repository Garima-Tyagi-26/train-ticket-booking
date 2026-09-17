package com.algoworks.trainticketbooking.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.security.Keys;

@Configuration
public class SecurityConfig {

	 private final String secretKey =  "mySecretKeyForTrainTicketBookingApplication123456";
	 
	 @Bean
	 public PasswordEncoder passwordEncoder() {
		 return new BCryptPasswordEncoder();
	 }
	
	 @Bean
	 public JwtDecoder jwtDecoder() {

	     SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
         return NimbusJwtDecoder.withSecretKey(key)
	             .macAlgorithm(MacAlgorithm.HS384)
	             .build();
	 }
	
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	        http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                    "/users",
	                    "/users/login",
	                    "/users/forgot-password",
	                    "/users/reset-password",
	                    "/swagger-ui/**",
	                    "/v3/api-docs/**"
	                ).permitAll()
	                
	                .requestMatchers(HttpMethod.POST, "/trains/**").hasRole("ADMIN")

	                .requestMatchers(HttpMethod.PUT, "/trains/**").hasRole("ADMIN")

	                .requestMatchers(HttpMethod.DELETE, "/trains/**").hasRole("ADMIN")

	                .anyRequest().authenticated()
	            )
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
	                                                              new JwtAuthenticationConverter())));
	         return http.build();
	    }
}
