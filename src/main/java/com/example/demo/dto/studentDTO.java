
package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class studentDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50,
          message = "First name must contain 2 to 50 characters")
    @Pattern(
        regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
        message = "First name must contain only letters, spaces, hyphens or apostrophes"
    )
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50,
          message = "Last name must contain 1 to 50 characters")
    @Pattern(
        regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
        message = "Last name must contain only letters, spaces, hyphens or apostrophes"
    )
    private String lastName;


    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;


    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Mobile number must contain exactly 10 digits and start with 6-9"
    )
    private String phone;


    @NotBlank(message = "Gender is required")
    @Pattern(
        regexp = "^(Male|Female|Other)$",
        message = "Gender must be Male, Female or Other"
    )
    private String gender;


    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;


    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100,
          message = "Department must contain 2 to 100 characters")
    @Pattern(
        regexp = "^[\\p{L}][\\p{L}\\s&-]*$",
        message = "Department must contain only letters, spaces, & or hyphens"
    )
    private String department;


    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200,
          message = "Address must contain 5 to 200 characters")
    @Pattern(
        regexp = "^[\\p{L}0-9][\\p{L}0-9\\s,.'/#-]*$",
        message = "Address contains invalid characters"
    )
    private String address;

}
