
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrderItemVo;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemVo, Long> {

	List<OrderItemVo> findByOrderId(Long orderId);

	List<OrderItemVo> findByOrderIdOrderByIdAsc(Long orderId);

	void deleteByOrderId(Long orderId);
}
