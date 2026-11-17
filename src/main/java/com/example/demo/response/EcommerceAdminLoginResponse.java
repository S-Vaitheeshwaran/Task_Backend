
package com.example.demo.response;

import lombok.Data;

@Data
public class EcommerceAdminLoginResponse {

	private String token;
	private String tokenType = "Bearer";
	private Long adminId;
	private String name;
	private String email;

}
