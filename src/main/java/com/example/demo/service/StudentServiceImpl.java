
package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.studentDTO;
import com.example.demo.entity.StudentVo;
import com.example.demo.repository.StudentRepository;
import com.example.demo.response.studentResponseDTO;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentRepository studentRepository;

	// ==========================================
	// CREATE / UPDATE STUDENT
	// ==========================================

	@Override
	@Transactional
	public Map<String, Object> createUpdateStudent(Long id, studentDTO student) {

		StudentVo studentVO;
		String message;

		// ==========================================
		// 1. CHECK DUPLICATE EMAIL
		// ==========================================

		String email = student.getEmail().trim();

		if (id == null) {

			// CREATE: Check whether email already exists
			if (studentRepository.existsByEmail(email)) {

				throw new RuntimeException("Email already exists: " + email);
			}

			studentVO = new StudentVo();
			message = "Student Created Successfully";

		} else {

			// UPDATE: Check email excluding current student
			if (studentRepository.existsByEmailAndIdNot(email, id)) {

				throw new RuntimeException("Email already belongs to another student: " + email);
			}

			// Find existing student
			studentVO = studentRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

			message = "Student Updated Successfully";
		}

		// ==========================================
		// 2. MAP DTO TO ENTITY
		// ==========================================

		studentVO.setFirstName(student.getFirstName());
		studentVO.setLastName(student.getLastName());
		studentVO.setEmail(email);
		studentVO.setPhone(student.getPhone());
		studentVO.setGender(student.getGender());
		studentVO.setDateOfBirth(student.getDateOfBirth());
		studentVO.setDepartment(student.getDepartment());
		studentVO.setAddress(student.getAddress());

		// ==========================================
		// 3. SAVE STUDENT
		// ==========================================

		StudentVo savedStudent = studentRepository.saveAndFlush(studentVO);

		// ==========================================
		// 4. CONVERT ENTITY TO RESPONSE DTO
		// ==========================================

		studentResponseDTO responseDTO = convertStudentToResponseDTO(savedStudent);

		// ==========================================
		// 5. FINAL RESPONSE
		// ==========================================

		Map<String, Object> response = new HashMap<>();

		response.put("message", message);
		response.put("studentVO", responseDTO);

		return response;
	}

	// ==========================================
	// CONVERT ENTITY TO RESPONSE DTO
	// ==========================================

	private studentResponseDTO convertStudentToResponseDTO(StudentVo studentVO) {

		studentResponseDTO responseDTO = new studentResponseDTO();

		responseDTO.setId(studentVO.getId());
		responseDTO.setFirstName(studentVO.getFirstName());
		responseDTO.setLastName(studentVO.getLastName());
		responseDTO.setEmail(studentVO.getEmail());
		responseDTO.setPhone(studentVO.getPhone());
		responseDTO.setGender(studentVO.getGender());
		responseDTO.setDateOfBirth(studentVO.getDateOfBirth());
		responseDTO.setDepartment(studentVO.getDepartment());
		responseDTO.setAddress(studentVO.getAddress());

		return responseDTO;
	}

	// ==========================================
	// GET STUDENT BY ID
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public studentResponseDTO getStudentById(Long id) {

		StudentVo studentVO = studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

		return convertStudentToResponseDTO(studentVO);
	}

	// ==========================================
	// GET ALL STUDENTS
	// ==========================================

	@Override
	@Transactional(readOnly = true)
	public List<StudentVo> getAllStudents() {

		return studentRepository.findAll();
	}

	// ==========================================
	// DELETE STUDENT
	// ==========================================

	@Override
	@Transactional
	public Map<String, Object> deleteStudent(Long id) {

		StudentVo studentVO = studentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

		studentRepository.delete(studentVO);

		Map<String, Object> response = new HashMap<>();

		response.put("message", "Student Deleted Successfully");
		response.put("id", id);

		return response;
	}
}
