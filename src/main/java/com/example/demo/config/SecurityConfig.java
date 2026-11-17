
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth

						// CORS preflight
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// =================================
						// FINANCE PUBLIC APIs
						// =================================
						.requestMatchers("/api/expense/auth/**").permitAll()

						// =================================
						// FINANCE PROTECTED APIs
						// =================================
						.requestMatchers("/api/expense/**").authenticated()

						// =================================
						// E-COMMERCE CUSTOMER PUBLIC APIs
						// =================================
						.requestMatchers("/api/ecommerce/accounts/register", "/api/ecommerce/accounts/login",
								"/api/ecommerce/accounts/forgot-password", "/api/ecommerce/accounts/reset-password")
						.permitAll()

						// =================================
						// E-COMMERCE ADMIN PUBLIC LOGIN
						// =================================
						.requestMatchers("/api/ecommerce/admin/login").permitAll()

						.requestMatchers(HttpMethod.GET, "/api/ecommerce/admin/customers")
						.hasAuthority("ROLE_ECOMMERCE_ADMIN")

						// =================================
						// E-COMMERCE PUBLIC PRODUCT APIs
						// =================================
						.requestMatchers(HttpMethod.GET, "/api/ecommerce/products", "/api/ecommerce/products/**")
						.permitAll()

						// =================================
						// E-COMMERCE ADMIN ONLY APIs
						// =================================
						.requestMatchers(HttpMethod.GET, "/api/ecommerce/orders").hasAuthority("ROLE_ECOMMERCE_ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/ecommerce/orders/*/status")
						.hasAuthority("ROLE_ECOMMERCE_ADMIN")

						// =================================
						// OTHER E-COMMERCE PROTECTED APIs
						// =================================
						.requestMatchers("/api/ecommerce/**").authenticated()

						// OTHER APIs
						.anyRequest().permitAll())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
