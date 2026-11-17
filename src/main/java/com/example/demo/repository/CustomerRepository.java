
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CustomerVo;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerVo, Long> {

	Optional<CustomerVo> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
