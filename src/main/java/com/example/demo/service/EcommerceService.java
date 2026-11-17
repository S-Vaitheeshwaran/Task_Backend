package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.CartRequest;
import com.example.demo.dto.CartUpdateRequest;
import com.example.demo.dto.CustomerLoginRequest;
import com.example.demo.dto.CustomerRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.ProductRequest;
import com.example.demo.entity.OrderStatus;
import com.example.demo.response.CartResponse;
import com.example.demo.response.CustomerAdminResponse;
import com.example.demo.response.CustomerLoginResponse;
import com.example.demo.response.CustomerResponse;
import com.example.demo.response.EcommerceAdminLoginResponse;
import com.example.demo.response.OrderResponse;
import com.example.demo.response.ProductResponse;

public interface EcommerceService {

	// Product Catalog
	ProductResponse createProduct(ProductRequest request);

	ProductResponse getProductById(Long id);

	List<ProductResponse> getAllProducts(String search, String category);

	ProductResponse updateProduct(Long id, ProductRequest request);

	void deleteProduct(Long id);

	// Shopping Cart
	CartResponse addToCart(CartRequest request);

	CartResponse getCart(Long customerId);

	CartResponse updateCartItem(Long itemId, CartUpdateRequest request);

	void removeCartItem(Long customerId, Long itemId);

	void clearCart(Long customerId);

	// Customer Accounts
	CustomerResponse registerCustomer(CustomerRequest request);

	CustomerLoginResponse loginCustomer(CustomerLoginRequest request);

	String forgotPassword(String email);

	String resetPassword(String email, String otp, String newPassword);

	CustomerResponse getCustomerProfile(String email);

	CustomerResponse updateCustomerProfile(Long id, CustomerRequest request);

	String changePassword(String email, String currentPassword, String newPassword, String confirmPassword);

	// Order Management
	OrderResponse placeOrder(String email, OrderRequest request);
	
	OrderResponse getOrderById(Long orderId, String email);

	List<OrderResponse> getCustomerOrders(String email);

	List<OrderResponse> getAllOrders();

	OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);

	OrderResponse cancelOrder(Long orderId, String email);

	// admin login

	EcommerceAdminLoginResponse loginEcommerceAdmin(Map<String, String> request);
	List<CustomerAdminResponse> getAllCustomers();

}