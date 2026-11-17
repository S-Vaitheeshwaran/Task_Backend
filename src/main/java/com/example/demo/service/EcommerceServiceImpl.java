
package com.example.demo.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CartRequest;
import com.example.demo.dto.CartUpdateRequest;
import com.example.demo.dto.CustomerLoginRequest;
import com.example.demo.dto.CustomerRequest;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.ProductRequest;
import com.example.demo.entity.CartItemVo;
import com.example.demo.entity.CartVo;
import com.example.demo.entity.CustomerVo;
import com.example.demo.entity.EcommerceAdminVo;
import com.example.demo.entity.OrderItemVo;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.OrderVo;
import com.example.demo.entity.ProductVo;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.EcommerceAdminRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.response.CartResponse;
import com.example.demo.response.CustomerAdminResponse;
import com.example.demo.response.CustomerLoginResponse;
import com.example.demo.response.CustomerResponse;
import com.example.demo.response.EcommerceAdminLoginResponse;
import com.example.demo.response.OrderItemResponse;
import com.example.demo.response.OrderResponse;
import com.example.demo.response.ProductResponse;
import com.example.demo.security.JwtService;

@Service
@Transactional
public class EcommerceServiceImpl implements EcommerceService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String mailFrom;

	@Value("${app.mail.sender-name}")
	private String senderName;

	private final SecureRandom secureRandom = new SecureRandom();

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private EcommerceAdminRepository ecommerceAdminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthService authService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Override
	public ProductResponse createProduct(ProductRequest request) {

		ProductVo product = new ProductVo();

		mapRequestToEntity(request, product);

		ProductVo savedProduct = productRepository.save(product);

		return mapToResponse(savedProduct);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long id) {

		ProductVo product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found: " + id));

		return mapToResponse(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts(String search, String category) {

		List<ProductVo> products;

		if (search != null && !search.isBlank()) {

			products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);

		} else if (category != null && !category.isBlank()) {

			products = productRepository.findByCategoryIgnoreCaseAndActiveTrue(category);

		} else {

			products = productRepository.findByActiveTrue();
		}

		return products.stream().map(this::mapToResponse).toList();
	}

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request) {

		ProductVo product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found: " + id));

		mapRequestToEntity(request, product);

		ProductVo updatedProduct = productRepository.save(product);

		return mapToResponse(updatedProduct);
	}

	@Override
	public void deleteProduct(Long id) {

		ProductVo product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found: " + id));

		product.setActive(false);

		productRepository.save(product);
	}

	// Request DTO to Entity mapping
	private void mapRequestToEntity(ProductRequest request, ProductVo product) {

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setCategory(request.getCategory());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setImageUrl(request.getImageUrl());

		if (request.getActive() != null) {
			product.setActive(request.getActive());
		} else if (product.getActive() == null) {
			product.setActive(true);
		}
	}

	// Entity to Response DTO mapping
	private ProductResponse mapToResponse(ProductVo product) {

		ProductResponse response = new ProductResponse();

		response.setId(product.getId());
		response.setName(product.getName());
		response.setDescription(product.getDescription());
		response.setCategory(product.getCategory());
		response.setPrice(product.getPrice());
		response.setStockQuantity(product.getStockQuantity());
		response.setImageUrl(product.getImageUrl());
		response.setActive(product.getActive());
		response.setCreatedAt(product.getCreatedAt());
		response.setUpdatedAt(product.getUpdatedAt());

		return response;
	}

	// ADD PRODUCT TO CART
	@Override
	public CartResponse addToCart(CartRequest request) {

		ProductVo product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found"));

		if (!Boolean.TRUE.equals(product.getActive())) {
			throw new RuntimeException("Product is not available");
		}

		if (request.getQuantity() > product.getStockQuantity()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient product stock");
		}

		CartVo cart = cartRepository.findByCustomerId(request.getCustomerId()).orElseGet(() -> {
			CartVo newCart = new CartVo();
			newCart.setCustomerId(request.getCustomerId());
			return cartRepository.save(newCart);
		});

		CartItemVo cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

		if (cartItem != null) {

			int updatedQuantity = cartItem.getQuantity() + request.getQuantity();

			if (updatedQuantity > product.getStockQuantity()) {
				throw new RuntimeException("Insufficient product stock");
			}

			cartItem.setQuantity(updatedQuantity);

		} else {

			cartItem = new CartItemVo();
			cartItem.setCart(cart);
			cartItem.setProduct(product);
			cartItem.setQuantity(request.getQuantity());
			cartItem.setUnitPrice(product.getPrice());

			cart.getItems().add(cartItem);
		}

		cartItemRepository.save(cartItem);

		return mapCartToResponse(cart);
	}

	// GET CUSTOMER CART
	@Override
	@Transactional(readOnly = true)
	public CartResponse getCart(Long customerId) {

		CartVo cart = cartRepository.findByCustomerId(customerId)
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		return mapCartToResponse(cart);
	}

	// UPDATE CART ITEM QUANTITY
	@Override
	public CartResponse updateCartItem(Long itemId, CartUpdateRequest request) {

		CartVo cart = cartRepository.findByCustomerId(request.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		CartItemVo cartItem = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
				.orElseThrow(() -> new RuntimeException("Cart item not found"));

		ProductVo product = cartItem.getProduct();

		if (!Boolean.TRUE.equals(product.getActive())) {
			throw new RuntimeException("Product is not available");
		}

		if (request.getQuantity() > product.getStockQuantity()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient product stock");
		}

		cartItem.setQuantity(request.getQuantity());

		cartItemRepository.save(cartItem);

		return mapCartToResponse(cart);
	}

	// REMOVE ITEM FROM CART
	@Override
	public void removeCartItem(Long customerId, Long itemId) {

		CartVo cart = cartRepository.findByCustomerId(customerId)
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		CartItemVo cartItem = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
				.orElseThrow(() -> new RuntimeException("Cart item not found"));

		cartItemRepository.delete(cartItem);
	}

	// CLEAR CUSTOMER CART
	@Override
	public void clearCart(Long customerId) {

		CartVo cart = cartRepository.findByCustomerId(customerId)
				.orElseThrow(() -> new RuntimeException("Cart not found"));

		cartItemRepository.deleteAll(cart.getItems());
	}

	// ENTITY TO CART RESPONSE CONVERSION
	private CartResponse mapCartToResponse(CartVo cart) {

		CartResponse response = new CartResponse();

		response.setCartId(cart.getId());
		response.setCustomerId(cart.getCustomerId());

		List<CartResponse.CartItemResponse> items = new ArrayList<>();

		int totalItems = 0;
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItemVo cartItem : cart.getItems()) {

			CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse();

			ProductVo product = cartItem.getProduct();

			BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			itemResponse.setCartItemId(cartItem.getId());
			itemResponse.setProductId(product.getId());
			itemResponse.setProductName(product.getName());
			itemResponse.setCategory(product.getCategory());
			itemResponse.setImageUrl(product.getImageUrl());
			itemResponse.setQuantity(cartItem.getQuantity());
			itemResponse.setUnitPrice(cartItem.getUnitPrice());
			itemResponse.setSubtotal(subtotal);

			items.add(itemResponse);

			totalItems += cartItem.getQuantity();
			totalAmount = totalAmount.add(subtotal);
		}

		response.setItems(items);
		response.setTotalItems(totalItems);
		response.setTotalAmount(totalAmount);

		return response;
	}

	@Override
	public CustomerResponse registerCustomer(CustomerRequest request) {

		if (request.getFirstName() == null || request.getFirstName().isBlank() || request.getLastName() == null
				|| request.getLastName().isBlank() || request.getEmail() == null || request.getEmail().isBlank()
				|| request.getPassword() == null || request.getPassword().isBlank()) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"First name, last name, email and password are required");
		}
		// 1. Normalize email
		String email = request.getEmail().trim().toLowerCase();

		// 2. Check whether email already exists
		if (customerRepository.existsByEmailIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		// 3. Create customer entity
		CustomerVo customer = new CustomerVo();

		customer.setFirstName(request.getFirstName().trim());
		customer.setLastName(request.getLastName().trim());
		customer.setEmail(email);
		customer.setPhone(request.getPhone());

		// 4. Encrypt password
		customer.setPassword(passwordEncoder.encode(request.getPassword()));

		customer.setActive(true);

		// 5. Save customer
		CustomerVo savedCustomer = customerRepository.save(customer);

		// 6. Return response DTO
		return mapCustomerToResponse(savedCustomer);
	}

	private CustomerResponse mapCustomerToResponse(CustomerVo customer) {

		CustomerResponse response = new CustomerResponse();

		response.setId(customer.getId());
		response.setFirstName(customer.getFirstName());
		response.setLastName(customer.getLastName());
		response.setEmail(customer.getEmail());
		response.setPhone(customer.getPhone());
		response.setActive(customer.getActive());
		response.setCreatedAt(customer.getCreatedAt());
		response.setUpdatedAt(customer.getUpdatedAt());

		return response;
	}

	@Override
	@Transactional
	public CustomerLoginResponse loginCustomer(CustomerLoginRequest request) {

		// 1. Normalize email
		String email = request.getEmail().trim().toLowerCase();

		// 2. Find customer
		CustomerVo customer = customerRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		// 3. Check account status
		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		// 4. Verify password
		if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		// 5. Generate JWT
		String token = jwtService.generateToken(customer.getEmail());

		// 6. Hash JWT using existing SHA-256 method
		String tokenHash = authService.hashToken(token);

		// 7. Get token expiry
		Instant expiry = jwtService.extractExpiration(token).toInstant();

		// 8. Save token hash and expiry in customer table
		customer.setActiveTokenHash(tokenHash);
		customer.setTokenExpiry(expiry);

		customerRepository.save(customer);

		// 9. Prepare login response
		CustomerLoginResponse response = new CustomerLoginResponse();

		response.setToken(token);
		response.setTokenType("Bearer");
		response.setCustomer(mapCustomerToResponse(customer));

		return response;
	}

	private void sendPasswordResetOtpEmail(String email, String otp) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(mailFrom);
		message.setTo(email);

		message.setSubject("E-Commerce - Password Reset OTP");

		message.setText("Hello,\n\n" + "We received a request to reset your E-Commerce account password.\n\n"
				+ "Your OTP is: " + otp + "\n\n" + "This OTP is valid for 5 minutes.\n\n"
				+ "Do not share this OTP with anyone.\n\n" + "Regards,\n" + senderName);

		mailSender.send(message);
	}

	@Override
	public String forgotPassword(String email) {

		if (email == null || email.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
		}

		String normalizedEmail = email.trim().toLowerCase();

		// Check whether email exists
		CustomerVo customer = customerRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Email does not exist. Please enter a valid email."));

		// Check customer account status
		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Your account is inactive. Please contact support.");
		}

		// Generate 6-digit OTP
		String otp = String.valueOf(secureRandom.nextInt(100000, 1000000));

		// Store hashed OTP
		customer.setResetOtpHash(authService.hashToken(otp));

		// Set OTP expiry to 5 minutes
		customer.setResetOtpExpiry(LocalDateTime.now().plusMinutes(5));

		customerRepository.save(customer);

		// Send OTP email
		sendPasswordResetOtpEmail(customer.getEmail(), otp);

		return "OTP sent successfully to your registered email address.";
	}

	@Override
	public String resetPassword(String email, String otp, String newPassword) {

		if (email == null || email.isBlank() || otp == null || otp.isBlank() || newPassword == null
				|| newPassword.isBlank()) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required");
		}

		if (!otp.matches("\\d{6}")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP must contain exactly 6 digits");
		}

		if (newPassword.length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain at least 8 characters");
		}

		String normalizedEmail = email.trim().toLowerCase();

		CustomerVo customer = customerRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP"));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		if (customer.getResetOtpHash() == null || customer.getResetOtpExpiry() == null) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
		}

		// Check OTP expiry
		if (LocalDateTime.now().isAfter(customer.getResetOtpExpiry())) {

			customer.setResetOtpHash(null);
			customer.setResetOtpExpiry(null);

			customerRepository.save(customer);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new OTP");
		}

		// Verify OTP
		String submittedOtpHash = authService.hashToken(otp);

		boolean otpMatches = MessageDigest.isEqual(customer.getResetOtpHash().getBytes(StandardCharsets.UTF_8),
				submittedOtpHash.getBytes(StandardCharsets.UTF_8));

		if (!otpMatches) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
		}

		// Update password using BCrypt
		customer.setPassword(passwordEncoder.encode(newPassword));

		// Clear OTP information
		customer.setResetOtpHash(null);
		customer.setResetOtpExpiry(null);

		// Invalidate existing login token
		customer.setActiveTokenHash(null);
		customer.setTokenExpiry(null);

		customerRepository.save(customer);

		return "Password reset successfully. Please login again.";
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerResponse getCustomerProfile(String email) {

		CustomerVo customer = customerRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found"));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		return mapCustomerToResponse(customer);
	}

	@Override
	public CustomerResponse updateCustomerProfile(Long id, CustomerRequest request) {

		// Find customer using ID
		CustomerVo customer = customerRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with ID: " + id));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		// Update first name if provided
		if (request.getFirstName() != null) {

			if (request.getFirstName().isBlank() || !request.getFirstName().trim().matches("^[a-zA-Z ]+$")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid first name");
			}

			if (request.getFirstName().trim().length() > 100) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name cannot exceed 100 characters");
			}

			customer.setFirstName(request.getFirstName().trim());
		}

		// Update last name if provided
		if (request.getLastName() != null) {

			if (request.getLastName().isBlank() || !request.getLastName().trim().matches("^[a-zA-Z ]+$")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid last name");
			}

			if (request.getLastName().trim().length() > 100) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name cannot exceed 100 characters");
			}

			customer.setLastName(request.getLastName().trim());
		}

		// Update phone if provided
		if (request.getPhone() != null) {

			if (!request.getPhone().matches("^[0-9]{10,15}$")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number must contain 10 to 15 digits");
			}

			customer.setPhone(request.getPhone());
		}

		// Email and password are not updated through this API.

		CustomerVo updatedCustomer = customerRepository.save(customer);

		return mapCustomerToResponse(updatedCustomer);
	}

	@Override
	public String changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {

		if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()
				|| confirmPassword == null || confirmPassword.isBlank()) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All password fields are required");
		}

		if (!newPassword.equals(confirmPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirm password do not match");
		}

		if (newPassword.length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"New password must contain at least 8 characters");
		}

		CustomerVo customer = customerRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
		}

		if (passwordEncoder.matches(newPassword, customer.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"New password must be different from the current password");
		}

		customer.setPassword(passwordEncoder.encode(newPassword));

		// Invalidate the existing login token
		customer.setActiveTokenHash(null);
		customer.setTokenExpiry(null);

		customerRepository.save(customer);

		return "Password changed successfully. Please login again.";
	}

	@Override
	@Transactional
	public OrderResponse placeOrder(String email, OrderRequest request) {

		// 1. Find customer using authenticated email
		CustomerVo customer = customerRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		// 2. Find customer cart
		CartVo cart = cartRepository.findByCustomerId(customer.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

		if (cart.getItems() == null || cart.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
		}

		// 3. Create order
		OrderVo order = new OrderVo();

		order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		order.setCustomer(customer);
		order.setOrderStatus(OrderStatus.PENDING);

		// 4. Save delivery information as an order snapshot
		order.setDeliveryName(request.getDeliveryName().trim());
		order.setDeliveryPhone(request.getDeliveryPhone().trim());
		order.setDeliveryEmail(request.getDeliveryEmail().trim().toLowerCase());

		order.setAddressLine1(request.getAddressLine1().trim());

		order.setAddressLine2(request.getAddressLine2() == null ? null : request.getAddressLine2().trim());

		order.setCity(request.getCity().trim());
		order.setState(request.getState().trim());
		order.setPostalCode(request.getPostalCode().trim());
		order.setCountry(request.getCountry().trim());

		order.setDeliveryInstructions(
				request.getDeliveryInstructions() == null ? null : request.getDeliveryInstructions().trim());

		BigDecimal totalAmount = BigDecimal.ZERO;

		// 5. Convert cart items into order items
		for (CartItemVo cartItem : cart.getItems()) {

			ProductVo product = productRepository.findById(cartItem.getProduct().getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

			Integer quantity = cartItem.getQuantity();

			if (quantity == null || quantity <= 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cart item quantity");
			}

			if (!Boolean.TRUE.equals(product.getActive())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, product.getName() + " is unavailable");
			}

			if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Insufficient stock for " + product.getName());
			}

			BigDecimal unitPrice = product.getPrice();

			BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

			OrderItemVo orderItem = new OrderItemVo();

			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setProductName(product.getName());
			orderItem.setQuantity(quantity);
			orderItem.setUnitPrice(unitPrice);
			orderItem.setSubtotal(subtotal);

			order.getItems().add(orderItem);

			totalAmount = totalAmount.add(subtotal);

			// 6. Reduce product stock
			product.setStockQuantity(product.getStockQuantity() - quantity);

			productRepository.save(product);
		}

		order.setTotalAmount(totalAmount);

		// 7. Save order and order items
		OrderVo savedOrder = orderRepository.save(order);

		// 8. Clear cart after successful order creation
		cartItemRepository.deleteAll(cart.getItems());
		cart.getItems().clear();

		cartRepository.save(cart);

		// 9. Return order response
		return mapOrderToResponse(savedOrder);
	}

	private OrderResponse mapOrderToResponse(OrderVo order) {

		OrderResponse response = new OrderResponse();

		response.setId(order.getId());
		response.setOrderNumber(order.getOrderNumber());
		response.setCustomerId(order.getCustomer().getId());
		response.setTotalAmount(order.getTotalAmount());
		response.setOrderStatus(order.getOrderStatus());
		response.setCreatedAt(order.getCreatedAt());
		response.setUpdatedAt(order.getUpdatedAt());
		
		// Map delivery details
		response.setDeliveryName(order.getDeliveryName());
		response.setDeliveryPhone(order.getDeliveryPhone());
		response.setDeliveryEmail(order.getDeliveryEmail());

		response.setAddressLine1(order.getAddressLine1());
		response.setAddressLine2(order.getAddressLine2());

		response.setCity(order.getCity());
		response.setState(order.getState());
		response.setPostalCode(order.getPostalCode());
		response.setCountry(order.getCountry());

		response.setDeliveryInstructions(order.getDeliveryInstructions());

		List<OrderItemResponse> itemResponses = new ArrayList<>();

		for (OrderItemVo item : order.getItems()) {

			OrderItemResponse itemResponse = new OrderItemResponse();

			itemResponse.setId(item.getId());
			itemResponse.setProductId(item.getProduct().getId());
			itemResponse.setProductName(item.getProductName());
			itemResponse.setQuantity(item.getQuantity());
			itemResponse.setUnitPrice(item.getUnitPrice());
			itemResponse.setSubtotal(item.getSubtotal());

			itemResponses.add(itemResponse);
		}

		response.setItems(itemResponses);

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long orderId, String email) {

		OrderVo order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		if (!order.getCustomer().getEmail().equalsIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this order");
		}

		return mapOrderToResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getCustomerOrders(String email) {

		CustomerVo customer = customerRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

		if (!Boolean.TRUE.equals(customer.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer account is inactive");
		}

		List<OrderVo> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());

		return orders.stream().map(this::mapOrderToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders() {

		List<OrderVo> orders = orderRepository.findAllByOrderByCreatedAtDesc();

		List<OrderResponse> responses = new ArrayList<>();

		for (OrderVo order : orders) {
			responses.add(mapOrderToResponse(order));
		}

		return responses;
	}

	@Override
	@Transactional
	public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {

		if (newStatus == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
		}

		OrderVo order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		OrderStatus currentStatus = order.getOrderStatus();

		if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This order cannot be updated");
		}

		boolean validTransition = (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED)
				|| (currentStatus == OrderStatus.CONFIRMED && newStatus == OrderStatus.PROCESSING)
				|| (currentStatus == OrderStatus.PROCESSING && newStatus == OrderStatus.SHIPPED)
				|| (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.DELIVERED);

		if (!validTransition) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid order status transition: " + currentStatus + " to " + newStatus);
		}

		order.setOrderStatus(newStatus);

		OrderVo updatedOrder = orderRepository.save(order);

		return mapOrderToResponse(updatedOrder);
	}

	@Override
	@Transactional
	public OrderResponse cancelOrder(Long orderId, String email) {

		// 1. Find the order
		OrderVo order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		// 2. Verify order ownership
		if (!order.getCustomer().getEmail().equalsIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this order");
		}

		// 3. Check whether cancellation is allowed
		OrderStatus currentStatus = order.getOrderStatus();

		if (currentStatus != OrderStatus.PENDING && currentStatus != OrderStatus.CONFIRMED) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This order cannot be cancelled");
		}

		// 4. Restore product stock
		for (OrderItemVo item : order.getItems()) {

			ProductVo product = productRepository.findById(item.getProduct().getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"Product not found: " + item.getProductName()));

			product.setStockQuantity(product.getStockQuantity() + item.getQuantity());

			productRepository.save(product);
		}

		// 5. Update order status
		order.setOrderStatus(OrderStatus.CANCELLED);

		OrderVo cancelledOrder = orderRepository.save(order);

		// 6. Return response
		return mapOrderToResponse(cancelledOrder);
	}
//Admin 

	@Override
	@Transactional
	public EcommerceAdminLoginResponse loginEcommerceAdmin(Map<String, String> request) {

		String email = request.get("email");
		String password = request.get("password");

		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
		}

		EcommerceAdminVo admin = ecommerceAdminRepository.findByEmailIgnoreCase(email.trim())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		if (!Boolean.TRUE.equals(admin.getActive())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin account is inactive");
		}

		if (!passwordEncoder.matches(password, admin.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		// Generate JWT
		String token = jwtService.generateToken(admin.getEmail());

		// Store only the token hash in the admin table
		admin.setActiveTokenHash(authService.hashToken(token));

		Instant expiry = jwtService.extractExpiration(token).toInstant();
		admin.setTokenExpiry(expiry);

		ecommerceAdminRepository.save(admin);

		// Prepare response
		EcommerceAdminLoginResponse response = new EcommerceAdminLoginResponse();

		response.setToken(token);
		response.setAdminId(admin.getId());
		response.setName(admin.getName());
		response.setEmail(admin.getEmail());

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerAdminResponse> getAllCustomers() {

		List<CustomerVo> customers = customerRepository.findAll();

		return customers.stream()
				.map(customer -> new CustomerAdminResponse(customer.getId(), customer.getFirstName(),
						customer.getLastName(), customer.getEmail(), customer.getPhone(), customer.getActive(),
						customer.getCreatedAt()))
				.toList();
	}
}
