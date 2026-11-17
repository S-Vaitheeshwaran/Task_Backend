package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ExpenseVo;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseVo, Long> {

    List<ExpenseVo> findAllByUser_Id(Long userId);

    Optional<ExpenseVo> findByIdAndUser_Id(Long id, Long userId);
}