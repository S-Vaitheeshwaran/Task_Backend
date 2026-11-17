
package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrderVo;

@Repository
public interface OrderRepository extends JpaRepository<OrderVo, Long> {

	Optional<OrderVo> findByOrderNumber(String orderNumber);

	List<OrderVo> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

	List<OrderVo> findAllByOrderByCreatedAtDesc();

	boolean existsByOrderNumber(String orderNumber);
}
