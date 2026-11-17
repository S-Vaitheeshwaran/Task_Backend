package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerLoginResponse {

    private String token;

    private String tokenType = "Bearer";

    private CustomerResponse customer;
}