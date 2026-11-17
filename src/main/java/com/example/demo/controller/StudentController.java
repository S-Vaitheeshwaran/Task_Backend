
package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.studentDTO;
import com.example.demo.entity.StudentVo;
import com.example.demo.response.ResponseDTO;
import com.example.demo.response.ResponseErrors;
import com.example.demo.response.studentResponseDTO;
import com.example.demo.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student")
public class StudentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

	@Autowired
	private StudentService studentService;

	// CREATE / UPDATE STUDENT
	@PutMapping("/createUpdateStudent")
	public ResponseEntity<ResponseDTO> createUpdateStudent(@Valid @RequestParam(required = false) Long id,
			@RequestBody studentDTO studentDTO) {

		String methodName = "createUpdateStudent()";
		LOGGER.debug("Starting method: {}", methodName);

		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO;

		try {
			Map<String, Object> studentResult = studentService.createUpdateStudent(id,studentDTO);

			responseObjectsMap.put("message", studentResult.get("message"));
			responseObjectsMap.put("studentVO", studentResult.get("studentVO"));

			responseDTO = createServiceResponse(responseObjectsMap);

		} catch (Exception e) {

			LOGGER.error("Error in {}: {}", methodName, e.getMessage(), e);

			responseDTO = createServiceResponseError(responseObjectsMap, "Student processing failed", e.getMessage());
		}

		LOGGER.debug("Ending method: {}", methodName);
		return ResponseEntity.ok(responseDTO);
	}

	// GET STUDENT BY ID
	@GetMapping("/getByStudentId")
	public ResponseEntity<ResponseDTO> getStudentById(@RequestParam Long id) {

		String methodName = "getStudentById()";
		LOGGER.debug("Starting method: {}", methodName);

		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO;

		try {
			studentResponseDTO studentResponse = studentService.getStudentById(id);

			responseObjectsMap.put("message", "Student retrieved successfully");

			responseObjectsMap.put("studentVO", studentResponse);

			responseDTO = createServiceResponse(responseObjectsMap);

		} catch (Exception e) {

			LOGGER.error("Error in {}: {}", methodName, e.getMessage(), e);

			responseDTO = createServiceResponseError(responseObjectsMap, "Student retrieval failed", e.getMessage());
		}

		LOGGER.debug("Ending method: {}", methodName);
		return ResponseEntity.ok(responseDTO);
	}

	// GET ALL STUDENTS
	@GetMapping("/getAllStudents")
	public ResponseEntity<ResponseDTO> getAllStudents() {

		String methodName = "getAllStudents()";
		LOGGER.debug("Starting method: {}", methodName);

		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO;

		try {
			List<StudentVo> students = studentService.getAllStudents();

			responseObjectsMap.put("message", "Students retrieved successfully");

			responseObjectsMap.put("students", students);

			responseDTO = createServiceResponse(responseObjectsMap);

		} catch (Exception e) {

			LOGGER.error("Error in {}: {}", methodName, e.getMessage(), e);

			responseDTO = createServiceResponseError(responseObjectsMap, "Students retrieval failed", e.getMessage());
		}

		LOGGER.debug("Ending method: {}", methodName);
		return ResponseEntity.ok(responseDTO);
	}

	// DELETE STUDENT
	@DeleteMapping("/deleteStudent")
	public ResponseEntity<ResponseDTO> deleteStudent(@RequestParam Long id) {

		String methodName = "deleteStudent()";
		LOGGER.debug("Starting method: {}", methodName);

		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO;

		try {
			Map<String, Object> deleteResult = studentService.deleteStudent(id);

			responseObjectsMap.put("message", deleteResult.get("message"));
			responseObjectsMap.put("id", deleteResult.get("id"));

			responseDTO = createServiceResponse(responseObjectsMap);

		} catch (Exception e) {

			LOGGER.error("Error in {}: {}", methodName, e.getMessage(), e);

			responseDTO = createServiceResponseError(responseObjectsMap, "Student deletion failed", e.getMessage());
		}

		LOGGER.debug("Ending method: {}", methodName);
		return ResponseEntity.ok(responseDTO);
	}

	// ==========================================
	// SUCCESS RESPONSE
	// ==========================================
	private ResponseDTO createServiceResponse(Map<String, Object> responseObjectsMap) {

		ResponseDTO responseDTO = new ResponseDTO();

		responseDTO.setStatus(true);
		responseDTO.setStatusFlag(ResponseDTO.OK);
		responseDTO.addAllObject(responseObjectsMap);

		return responseDTO;
	}

	// ==========================================
	// ERROR RESPONSE
	// ==========================================
	private ResponseDTO createServiceResponseError(Map<String, Object> responseObjectsMap, String message,
			String errorMessage) {

		ResponseDTO responseDTO = new ResponseDTO();

		responseDTO.setStatus(false);
		responseDTO.setStatusFlag(ResponseDTO.ERROR);

		ResponseErrors responseErrors = new ResponseErrors();
		responseErrors.setMessage(message);
		responseErrors.setDetails(errorMessage);

		responseDTO.getErrors().add(responseErrors);

		responseDTO.addAllObject(responseObjectsMap);

		return responseDTO;
	}
}
