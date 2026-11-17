
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartVo;

@Repository
public interface CartRepository extends JpaRepository<CartVo, Long> {

    Optional<CartVo> findByCustomerId(Long customerId);
}
