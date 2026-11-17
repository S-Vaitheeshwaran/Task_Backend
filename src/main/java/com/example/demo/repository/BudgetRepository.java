package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BudgetVo;

public interface BudgetRepository extends JpaRepository<BudgetVo, Long> {

    List<BudgetVo> findAllByUser_Id(Long userId);

    Optional<BudgetVo> findByIdAndUser_Id(Long id, Long userId);
}