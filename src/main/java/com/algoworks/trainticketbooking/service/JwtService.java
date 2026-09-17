package com.algoworks.trainticketbooking.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	   private final String secretKey = "mySecretKeyForTrainTicketBookingApplication123456";

	    public String generateToken(String email, String role) {

	        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

	        return Jwts.builder()
	                .subject(email)
	                .claim("role", role)
	                .issuedAt(new Date())
	                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
	                .signWith(key, Jwts.SIG.HS384)
	                .compact();
	    }
}
