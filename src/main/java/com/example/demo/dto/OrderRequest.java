
package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class OrderRequest {

	@NotBlank(message = "Delivery name is required")
	@Size(max = 150, message = "Name cannot exceed 150 characters")
	private String deliveryName;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits")
	private String deliveryPhone;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	@Size(max = 150)
	private String deliveryEmail;

	@NotBlank(message = "Address line 1 is required")
	@Size(max = 255)
	private String addressLine1;

	@Size(max = 255)
	private String addressLine2;

	@NotBlank(message = "City is required")
	@Size(max = 100)
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 100)
	private String state;

	@NotBlank(message = "PIN code is required")
	@Pattern(regexp = "^[A-Za-z0-9 -]{3,12}$", message = "Enter a valid PIN or postal code")
	private String postalCode;

	@NotBlank(message = "Country is required")
	@Size(max = 100)
	private String country;

	@Size(max = 500)
	private String deliveryInstructions;
}
