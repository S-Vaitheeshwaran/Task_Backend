
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EcommerceAdminVo;

@Repository
public interface EcommerceAdminRepository extends JpaRepository<EcommerceAdminVo, Long> {

	Optional<EcommerceAdminVo> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
