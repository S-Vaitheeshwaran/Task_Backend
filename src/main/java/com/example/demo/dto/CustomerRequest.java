
package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {

	@Size(max = 100, message = "First name cannot exceed 100 characters")
	@Pattern(regexp = "^[a-zA-Z ]+$", message = "First name must contain only letters")
	private String firstName;

	@Size(max = 100, message = "Last name cannot exceed 100 characters")
	@Pattern(regexp = "^[a-zA-Z ]+$", message = "Last name must contain only letters")
	private String lastName;

	@Email(message = "Enter a valid email address")
	private String email;

	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits")
	private String phone;

	@Size(min = 8, message = "Password must contain at least 8 characters")
	private String password;
}
