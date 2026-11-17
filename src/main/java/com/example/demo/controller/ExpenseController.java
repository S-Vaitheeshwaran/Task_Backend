
package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.demo.dto.expenseDTO;
import com.example.demo.dto.incomeDTO;
import com.example.demo.dto.budgetDTO;

import com.example.demo.response.ResponseDTO;
import com.example.demo.response.ResponseErrors;

import com.example.demo.service.ExpenseService;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

	private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

	@Autowired
	private ExpenseService expenseService;

	// =====================================================
	// COMMON RESPONSE METHODS
	// =====================================================

	private ResponseEntity<ResponseDTO> createServiceResponse(Map<String, Object> responseObjectsMap) {

		ResponseDTO responseDTO = new ResponseDTO();

		responseDTO.setStatus(true);
		responseDTO.setStatusFlag(ResponseDTO.OK);
		responseDTO.addAllObject(responseObjectsMap);

		return ResponseEntity.ok(responseDTO);
	}

	private ResponseEntity<ResponseDTO> createServiceResponseError(Exception e,
			Map<String, Object> responseObjectsMap) {

		ResponseDTO responseDTO = new ResponseDTO();

		responseDTO.setStatus(false);
		responseDTO.setStatusFlag(ResponseDTO.ERROR);

		ResponseErrors responseErrors = new ResponseErrors();
		responseErrors.setMessage("ERROR");
		responseErrors.setDetails(e.getMessage());

		responseDTO.getErrors().add(responseErrors);
		responseDTO.addAllObject(responseObjectsMap);

		return ResponseEntity.ok(responseDTO);
	}

	// =====================================================
	// EXPENSE APIs
	// =====================================================

	@PutMapping("/createUpdateExpense")
	public ResponseEntity<ResponseDTO> createUpdateExpense(@RequestParam(required = false) Long id,
			@Valid @RequestBody expenseDTO expense) {

		logger.info("Inside createUpdateExpense");

		try {
			Map<String, Object> response = expenseService.createUpdateExpense(id, expense);

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in createUpdateExpense", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getByExpenseId")
	public ResponseEntity<ResponseDTO> getExpenseById(@RequestParam Long id) {

		logger.info("Inside getExpenseById");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("expenseVO", expenseService.getExpenseById(id));

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getExpenseById", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getAllExpenses")
	public ResponseEntity<ResponseDTO> getAllExpenses() {

		logger.info("Inside getAllExpenses");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("expenses", expenseService.getAllExpenses());

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getAllExpenses", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@DeleteMapping("/deleteExpense")
	public ResponseEntity<ResponseDTO> deleteExpense(@RequestParam Long id) {

		logger.info("Inside deleteExpense");

		try {
			return createServiceResponse(expenseService.deleteExpense(id));

		} catch (Exception e) {
			logger.error("Error in deleteExpense", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getExpenseSummary")
	public ResponseEntity<ResponseDTO> getExpenseSummary() {

		logger.info("Inside getExpenseSummary");

		try {
			return createServiceResponse(expenseService.getExpenseSummary());

		} catch (Exception e) {
			logger.error("Error in getExpenseSummary", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	// =====================================================
	// INCOME APIs
	// =====================================================

	@PutMapping("/createUpdateIncome")
	public ResponseEntity<ResponseDTO> createUpdateIncome(@RequestParam(required = false) Long id,
			@Valid @RequestBody incomeDTO income) {

		logger.info("Inside createUpdateIncome");

		try {
			Map<String, Object> response = expenseService.createUpdateIncome(id, income);

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in createUpdateIncome", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getByIncomeId")
	public ResponseEntity<ResponseDTO> getIncomeById(@RequestParam Long id) {

		logger.info("Inside getIncomeById");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("incomeVO", expenseService.getIncomeById(id));

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getIncomeById", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getAllIncomes")
	public ResponseEntity<ResponseDTO> getAllIncomes() {

		logger.info("Inside getAllIncomes");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("incomes", expenseService.getAllIncomes());

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getAllIncomes", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@DeleteMapping("/deleteIncome")
	public ResponseEntity<ResponseDTO> deleteIncome(@RequestParam Long id) {

		logger.info("Inside deleteIncome");

		try {
			return createServiceResponse(expenseService.deleteIncome(id));

		} catch (Exception e) {
			logger.error("Error in deleteIncome", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getIncomeSummary")
	public ResponseEntity<ResponseDTO> getIncomeSummary() {

		logger.info("Inside getIncomeSummary");

		try {
			return createServiceResponse(expenseService.getIncomeSummary());

		} catch (Exception e) {
			logger.error("Error in getIncomeSummary", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	// =====================================================
	// BUDGET APIs
	// =====================================================

	@PutMapping("/createUpdateBudget")
	public ResponseEntity<ResponseDTO> createUpdateBudget(@RequestParam(required = false) Long id,
			@Valid @RequestBody budgetDTO budget) {

		logger.info("Inside createUpdateBudget");

		try {
			Map<String, Object> response = expenseService.createUpdateBudget(id, budget);

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in createUpdateBudget", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getBudgetById")
	public ResponseEntity<ResponseDTO> getBudgetById(@RequestParam Long id) {

		logger.info("Inside getBudgetById");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("budgetVO", expenseService.getBudgetById(id));

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getBudgetById", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@GetMapping("/getAllBudgets")
	public ResponseEntity<ResponseDTO> getAllBudgets(@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Integer year) {

		logger.info("Inside getAllBudgets");

		try {
			Map<String, Object> response = new HashMap<>();

			response.put("budgets", expenseService.getAllBudgets(month, year));

			return createServiceResponse(response);

		} catch (Exception e) {
			logger.error("Error in getAllBudgets", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	@DeleteMapping("/deleteBudget")
	public ResponseEntity<ResponseDTO> deleteBudget(@RequestParam Long id) {

		logger.info("Inside deleteBudget");

		try {
			return createServiceResponse(expenseService.deleteBudget(id));

		} catch (Exception e) {
			logger.error("Error in deleteBudget", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}

	// =====================================================
	// FINANCIAL DASHBOARD API
	// =====================================================

	@GetMapping("/getFinanceSummary")
	public ResponseEntity<ResponseDTO> getFinanceSummary(@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Integer year) {

		logger.info("Inside getFinanceSummary");

		try {
			return createServiceResponse(expenseService.getFinanceSummary(month, year));

		} catch (Exception e) {
			logger.error("Error in getFinanceSummary", e);
			return createServiceResponseError(e, new HashMap<>());
		}
	}
}
