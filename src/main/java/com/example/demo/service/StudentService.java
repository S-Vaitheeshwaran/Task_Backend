
package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.entity.StudentVo;
import com.example.demo.response.studentResponseDTO;
import com.example.demo.dto.studentDTO;

public interface StudentService {

    // Create update Student
	Map<String, Object> createUpdateStudent(Long id,studentDTO student);
    // Get Student By ID
    studentResponseDTO getStudentById(Long id);

    // Get All Students
    List<StudentVo> getAllStudents();

    // Delete Student
    Map<String, Object> deleteStudent(Long id);
}
