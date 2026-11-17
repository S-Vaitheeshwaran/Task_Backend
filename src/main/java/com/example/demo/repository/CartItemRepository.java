
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartItemVo;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemVo, Long> {

    Optional<CartItemVo> findByIdAndCartId(Long id, Long cartId);

    Optional<CartItemVo> findByCartIdAndProductId(
            Long cartId, Long productId);
}
