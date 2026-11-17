
package com.example.demo.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
import com.example.demo.service.EcommerceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ecommerce")
public class EcommerceController {

	private final EcommerceService ecommerceService;

	public EcommerceController(EcommerceService ecommerceService) {
		this.ecommerceService = ecommerceService;
	}

	// Standard response structure
	private Map<String, Object> createSuccessResponse(String message, String key, Object data) {

		Map<String, Object> paramObjectsMap = new LinkedHashMap<>();
		paramObjectsMap.put("message", message);

		if (key != null && data != null) {
			paramObjectsMap.put(key, data);
		}

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("paramObjectsMap", paramObjectsMap);
		response.put("status", true);
		response.put("statusFlag", "Ok");

		return response;
	}

	// CREATE PRODUCT
	@PostMapping("/products")
	public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductRequest request) {

		ProductResponse product = ecommerceService.createProduct(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(createSuccessResponse("Product created successfully", "product", product));
	}

	// GET ALL PRODUCTS
	@GetMapping("/products")
	public ResponseEntity<Map<String, Object>> getAllProducts(@RequestParam(required = false) String search,
			@RequestParam(required = false) String category) {

		List<ProductResponse> products = ecommerceService.getAllProducts(search, category);

		return ResponseEntity.ok(createSuccessResponse("Products retrieved successfully", "products", products));
	}

	// GET PRODUCT BY ID
	@GetMapping("/products/{id}")
	public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {

		ProductResponse product = ecommerceService.getProductById(id);

		return ResponseEntity.ok(createSuccessResponse("Product retrieved successfully", "product", product));
	}

	// UPDATE PRODUCT
	@PutMapping("/products/{id}")
	public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id,
			@Valid @RequestBody ProductRequest request) {

		ProductResponse product = ecommerceService.updateProduct(id, request);

		return ResponseEntity.ok(createSuccessResponse("Product updated successfully", "product", product));
	}

	// DELETE PRODUCT
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {

		ecommerceService.deleteProduct(id);

		return ResponseEntity.ok(createSuccessResponse("Product deleted successfully", null, null));
	}

	// ADD PRODUCT TO CART
	@PostMapping("/cart/items")
	public ResponseEntity<Map<String, Object>> addToCart(@Valid @RequestBody CartRequest request) {

		CartResponse cart = ecommerceService.addToCart(request);

		return ResponseEntity.ok(createSuccessResponse("Product added to cart successfully", "cart", cart));
	}

	// GET CUSTOMER CART
	@GetMapping("/cart/{customerId}")
	public ResponseEntity<Map<String, Object>> getCart(@PathVariable Long customerId) {

		CartResponse cart = ecommerceService.getCart(customerId);

		return ResponseEntity.ok(createSuccessResponse("Cart retrieved successfully", "cart", cart));
	}

	// UPDATE CART ITEM
	@PutMapping("/cart/items/{itemId}")
	public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable Long itemId,
			@Valid @RequestBody CartUpdateRequest request) {

		CartResponse cart = ecommerceService.updateCartItem(itemId, request);

		return ResponseEntity.ok(createSuccessResponse("Cart updated successfully", "cart", cart));
	}

	// REMOVE CART ITEM
	@DeleteMapping("/cart/{customerId}/items/{itemId}")
	public ResponseEntity<Map<String, Object>> removeCartItem(@PathVariable Long customerId,
			@PathVariable Long itemId) {

		ecommerceService.removeCartItem(customerId, itemId);

		return ResponseEntity.ok(createSuccessResponse("Cart item removed successfully", null, null));
	}

	// CLEAR CART
	@DeleteMapping("/cart/{customerId}")
	public ResponseEntity<Map<String, Object>> clearCart(@PathVariable Long customerId) {

		ecommerceService.clearCart(customerId);

		return ResponseEntity.ok(createSuccessResponse("Cart cleared successfully", null, null));
	}

	@PostMapping("/accounts/register")
	public ResponseEntity<Map<String, Object>> registerCustomer(@Valid @RequestBody CustomerRequest request) {

		CustomerResponse customer = ecommerceService.registerCustomer(request);

		return ResponseEntity.ok(createSuccessResponse("Customer registered successfully", "customer", customer));
	}

	@PostMapping("/accounts/login")
	public ResponseEntity<Map<String, Object>> loginCustomer(@Valid @RequestBody CustomerLoginRequest request) {

		CustomerLoginResponse customer = ecommerceService.loginCustomer(request);

		return ResponseEntity.ok(createSuccessResponse("Customer login successful", "customer", customer));
	}

	// FORGOT PASSWORD
	@PostMapping("/accounts/forgot-password")
	public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {

		String email = request.get("email");

		String message = ecommerceService.forgotPassword(email);

		return ResponseEntity.ok(createSuccessResponse(message, null, null));
	}

	// RESET PASSWORD
	@PostMapping("/accounts/reset-password")
	public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {

		String email = request.get("email");
		String otp = request.get("otp");
		String newPassword = request.get("newPassword");

		String message = ecommerceService.resetPassword(email, otp, newPassword);

		return ResponseEntity.ok(createSuccessResponse(message, null, null));
	}

	// GET CUSTOMER PROFILE
	@GetMapping("/accounts/profile")
	public ResponseEntity<Map<String, Object>> getCustomerProfile(Authentication authentication) {

		String email = authentication.getName();

		CustomerResponse customer = ecommerceService.getCustomerProfile(email);

		return ResponseEntity
				.ok(createSuccessResponse("Customer profile retrieved successfully", "customer", customer));
	}

	// UPDATE CUSTOMER PROFILE
	@PutMapping("/accounts/profile/{id}")
	public ResponseEntity<Map<String, Object>> updateCustomerProfile(@PathVariable Long id,
			@RequestBody CustomerRequest request) {

		CustomerResponse customer = ecommerceService.updateCustomerProfile(id, request);

		return ResponseEntity.ok(createSuccessResponse("Customer profile updated successfully", "customer", customer));
	}

	@PutMapping("/accounts/change-password")
	public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
		}

		String message = ecommerceService.changePassword(authentication.getName(), request.get("currentPassword"),
				request.get("newPassword"), request.get("confirmPassword"));

		return ResponseEntity.ok(createSuccessResponse(message, null, null));
	}

	@PostMapping("/orders/place")
	public ResponseEntity<Map<String, Object>> placeOrder(@Valid @RequestBody OrderRequest request,
			Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
		}

		OrderResponse order = ecommerceService.placeOrder(authentication.getName(), request);

		return ResponseEntity.ok(createSuccessResponse("Order placed successfully", "order", order));
	}

	@GetMapping("/orders/{id}")
	public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id, Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
		}

		OrderResponse order = ecommerceService.getOrderById(id, authentication.getName());

		return ResponseEntity.ok(createSuccessResponse("Order retrieved successfully", "order", order));
	}

	@GetMapping("/orders/MyOrders")
	public ResponseEntity<Map<String, Object>> getCustomerOrders(Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
		}

		List<OrderResponse> orders = ecommerceService.getCustomerOrders(authentication.getName());

		return ResponseEntity.ok(createSuccessResponse("Customer orders retrieved successfully", "orders", orders));
	}

	@GetMapping("/orders")
	public ResponseEntity<Map<String, Object>> getAllOrders(Authentication authentication) {

		if (authentication == null || !authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ECOMMERCE_ADMIN"))) {

			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
		}

		List<OrderResponse> orders = ecommerceService.getAllOrders();

		return ResponseEntity.ok(createSuccessResponse("All orders retrieved successfully", "orders", orders));
	}

	@PutMapping("/orders/{id}/status")
	public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable Long id,
			@RequestBody Map<String, String> request, Authentication authentication) {

		if (authentication == null || !authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ECOMMERCE_ADMIN"))) {

			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
		}

		String statusValue = request.get("status");

		if (statusValue == null || statusValue.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
		}

		OrderStatus newStatus;

		try {
			newStatus = OrderStatus.valueOf(statusValue.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
		}

		OrderResponse order = ecommerceService.updateOrderStatus(id, newStatus);

		return ResponseEntity.ok(createSuccessResponse("Order status updated successfully", "order", order));
	}

	@PutMapping("/orders/{id}/cancel")
	public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id, Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
		}

		OrderResponse order = ecommerceService.cancelOrder(id, authentication.getName());

		return ResponseEntity.ok(createSuccessResponse("Order cancelled successfully", "order", order));
	}

	@PostMapping("/admin/login")
	public ResponseEntity<Map<String, Object>> loginEcommerceAdmin(@RequestBody Map<String, String> request) {

		EcommerceAdminLoginResponse admin = ecommerceService.loginEcommerceAdmin(request);

		return ResponseEntity.ok(createSuccessResponse("E-Commerce Admin login successful", "admin", admin));
	}

	@GetMapping("/admin/customers")
	public ResponseEntity<?> getAllCustomers() {

		List<CustomerAdminResponse> customers = ecommerceService.getAllCustomers();

		return ResponseEntity.ok(Map.of("status", true, "statusFlag", "Ok", "paramObjectsMap",
				Map.of("message", "Customers retrieved successfully", "customers", customers)));
	}
}
