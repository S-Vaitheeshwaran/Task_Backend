
package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.UserVo;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.AuthResponseDTO;
import com.example.demo.security.JwtService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponseDTO register(RegisterRequest request) {

		String email = request.getEmail().trim().toLowerCase();

		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("Email is already registered");
		}

		UserVo user = new UserVo();

		user.setName(request.getName().trim());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("USER");

		user = userRepository.save(user);

		return createAuthResponse(user);
	}

	@Transactional
	public AuthResponseDTO login(LoginRequest request) {

		UserVo user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

			throw new RuntimeException("Invalid email or password");
		}

		return createAuthResponse(user);
	}

	// Generate token, hash it and save it in users table
	private AuthResponseDTO createAuthResponse(UserVo user) {

		String token = jwtService.generateToken(user.getEmail());

		String tokenHash = hashToken(token);

		Instant expiry = jwtService.extractExpiration(token).toInstant();

		user.setActiveTokenHash(tokenHash);
		user.setTokenExpiry(expiry);

		userRepository.save(user);

		return new AuthResponseDTO(token, "Bearer", user.getId(), user.getName(), user.getEmail(), user.getRole());
	}

	// SHA-256 token hashing
	public String hashToken(String token) {

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

			return HexFormat.of().formatHex(hash);

		} catch (NoSuchAlgorithmException e) {

			throw new IllegalStateException("Unable to hash JWT token", e);
		}
	}

	// Backend logout
	@Transactional
	public void logout(String token) {

		if (!jwtService.isTokenValid(token)) {
			throw new RuntimeException("Invalid or expired token");
		}

		String email = jwtService.extractEmail(token);

		UserVo user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		String tokenHash = hashToken(token);

		if (user.getActiveTokenHash() == null || !user.getActiveTokenHash().equals(tokenHash)) {

			throw new RuntimeException("Token is already invalid");
		}

		user.setActiveTokenHash(null);
		user.setTokenExpiry(null);

		userRepository.save(user);
	}

	// Validate token against the database
	@Transactional(readOnly = true)
	public boolean isTokenActive(String token) {

		if (!jwtService.isTokenValid(token)) {
			return false;
		}

		String email = jwtService.extractEmail(token);

		UserVo user = userRepository.findByEmail(email).orElse(null);

		if (user == null || user.getActiveTokenHash() == null) {
			return false;
		}

		String tokenHash = hashToken(token);

		return user.getActiveTokenHash().equals(tokenHash) && user.getTokenExpiry() != null
				&& user.getTokenExpiry().isAfter(Instant.now());
	}
}
