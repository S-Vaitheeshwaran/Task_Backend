
package com.example.demo.security;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entity.CustomerVo;
import com.example.demo.entity.EcommerceAdminVo;
import com.example.demo.entity.UserVo;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.EcommerceAdminRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final CustomerRepository customerRepository;
	private final EcommerceAdminRepository ecommerceAdminRepository;

	// Constructor injection
	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository, AuthService authService,
			CustomerRepository customerRepository, EcommerceAdminRepository ecommerceAdminRepository) {

		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.authService = authService;
		this.customerRepository = customerRepository;
		this.ecommerceAdminRepository = ecommerceAdminRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Allow CORS preflight requests
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");

		// Continue if no Bearer token is supplied
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		try {

			// Validate JWT signature and expiration
			if (!jwtService.isTokenValid(token)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
				return;
			}

			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				String email = jwtService.extractEmail(token);

				List<SimpleGrantedAuthority> authorities;

				// ==========================================
				// E-COMMERCE AUTHENTICATION
				// ==========================================

				if (request.getRequestURI().startsWith("/api/ecommerce/")) {

					String tokenHash = authService.hashToken(token);

					// 1. Check E-Commerce Customer
					CustomerVo customer = customerRepository.findByEmailIgnoreCase(email).orElse(null);

					boolean validCustomerToken = customer != null && Boolean.TRUE.equals(customer.getActive())
							&& customer.getActiveTokenHash() != null && customer.getActiveTokenHash().equals(tokenHash)
							&& customer.getTokenExpiry() != null && customer.getTokenExpiry().isAfter(Instant.now());

					if (validCustomerToken) {

						authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

					} else {

						// 2. Check separate E-Commerce Admin
						EcommerceAdminVo admin = ecommerceAdminRepository.findByEmailIgnoreCase(email).orElse(null);

						boolean validAdminToken = admin != null && Boolean.TRUE.equals(admin.getActive())
								&& admin.getActiveTokenHash() != null && admin.getActiveTokenHash().equals(tokenHash)
								&& admin.getTokenExpiry() != null && admin.getTokenExpiry().isAfter(Instant.now());

						if (!validAdminToken) {

							response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
									"Invalid E-Commerce customer or admin token");
							return;
						}

						authorities = List.of(new SimpleGrantedAuthority("ROLE_ECOMMERCE_ADMIN"));
					}

				} else {

					// ==========================================
					// EXISTING FINANCE AUTHENTICATION
					// KEEPING EXISTING LOGIC UNCHANGED
					// ==========================================

					if (!authService.isTokenActive(token)) {

						response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
								"Token is invalid, expired or logged out");
						return;
					}

					UserVo user = userRepository.findByEmail(email).orElse(null);

					if (user == null) {

						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
						return;
					}

					// Preserve existing Finance authority behavior
					authorities = List.of();
				}

				// Set authenticated user in SecurityContext
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
						null, authorities);

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {

			SecurityContextHolder.clearContext();

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");

			return;
		}

		filterChain.doFilter(request, response);
	}
}
