
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ProductVo;

@Repository
public interface ProductRepository extends JpaRepository<ProductVo, Long> {

	List<ProductVo> findByActiveTrue();

	List<ProductVo> findByCategoryIgnoreCaseAndActiveTrue(String category);

	List<ProductVo> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
