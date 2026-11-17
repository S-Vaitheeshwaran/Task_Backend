package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.StudentVo;

@Repository
public interface StudentRepository extends JpaRepository<StudentVo, Long> 
{
	  boolean existsByEmail(String email);

	  boolean existsByEmailAndIdNot(String email, Long id);
}
