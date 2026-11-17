
package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.response.ResponseDTO;
import com.example.demo.response.ResponseErrors;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expense/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequest request) {

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("auth", authService.register(request));

			return createSuccessResponse(response);

		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginRequest request) {

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("auth", authService.login(request));

			return createSuccessResponse(response);

		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	private ResponseEntity<ResponseDTO> createSuccessResponse(Map<String, Object> responseMap) {

		ResponseDTO response = new ResponseDTO();

		response.setStatus(true);
		response.setStatusFlag(ResponseDTO.OK);
		response.addAllObject(responseMap);

		return ResponseEntity.ok(response);
	}

	private ResponseEntity<ResponseDTO> createErrorResponse(Exception e) {

		ResponseDTO response = new ResponseDTO();

		response.setStatus(false);
		response.setStatusFlag(ResponseDTO.ERROR);

		ResponseErrors error = new ResponseErrors();
		error.setMessage("ERROR");
		error.setDetails(e.getMessage());

		response.getErrors().add(error);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body("Authorization token is missing");
		}

		try {

			String token = authHeader.substring(7);

			authService.logout(token);

			return ResponseEntity.ok(Map.of("message", "Logout successful"));

		} catch (RuntimeException e) {

			return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
		}
	}

}
