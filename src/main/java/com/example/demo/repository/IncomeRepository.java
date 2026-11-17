package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.IncomeVo;

public interface IncomeRepository extends JpaRepository<IncomeVo, Long> {

    List<IncomeVo> findAllByUser_Id(Long userId);

    Optional<IncomeVo> findByIdAndUser_Id(Long id, Long userId);
}