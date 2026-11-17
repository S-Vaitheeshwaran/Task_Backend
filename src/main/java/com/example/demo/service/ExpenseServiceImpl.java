
package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.expenseDTO;
import com.example.demo.dto.incomeDTO;
import com.example.demo.dto.budgetDTO;

import com.example.demo.response.expenseResponseDTO;
import com.example.demo.response.incomeResponseDTO;

import com.example.demo.entity.ExpenseVo;
import com.example.demo.entity.IncomeVo;
import com.example.demo.entity.BudgetVo;
import com.example.demo.entity.UserVo;

import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.IncomeRepository;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private IncomeRepository incomeRepository;

	@Autowired
	private BudgetRepository budgetRepository;

	@Autowired
	private UserRepository userRepository;

	// =====================================================
	// CURRENT AUTHENTICATED USER
	// =====================================================

	private UserVo getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getName())) {

			throw new RuntimeException("User is not authenticated");
		}

		String email = authentication.getName();

		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Authenticated user not found"));
	}

	private ExpenseVo getExpenseForCurrentUser(Long id, Long userId) {

		return expenseRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new RuntimeException("Expense not found"));
	}

	private IncomeVo getIncomeForCurrentUser(Long id, Long userId) {

		return incomeRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new RuntimeException("Income not found"));
	}

	private BudgetVo getBudgetForCurrentUser(Long id, Long userId) {

		return budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new RuntimeException("Budget not found"));
	}

	// =====================================================
	// EXPENSE MODULE
	// =====================================================

	@Override
	public Map<String, Object> createUpdateExpense(Long id, expenseDTO expense) {

		UserVo user = getCurrentUser();

		ExpenseVo expenseVo;

		if (id != null) {

			// Update only the logged-in user's expense
			expenseVo = getExpenseForCurrentUser(id, user.getId());

		} else {

			expenseVo = new ExpenseVo();

			// Assign the logged-in user
			expenseVo.setUser(user);
		}

		expenseVo.setTitle(expense.getTitle());
		expenseVo.setDescription(expense.getDescription());
		expenseVo.setAmount(expense.getAmount());
		expenseVo.setCategory(expense.getCategory());
		expenseVo.setExpenseDate(expense.getExpenseDate());
		expenseVo.setPaymentMethod(expense.getPaymentMethod());
		expenseVo.setNotes(expense.getNotes());

		expenseVo = expenseRepository.saveAndFlush(expenseVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", id == null ? "Expense created successfully" : "Expense updated successfully");

		response.put("expenseVO", convertExpenseToResponse(expenseVo));

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public expenseResponseDTO getExpenseById(Long id) {

		UserVo user = getCurrentUser();

		ExpenseVo expenseVo = getExpenseForCurrentUser(id, user.getId());

		return convertExpenseToResponse(expenseVo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<expenseResponseDTO> getAllExpenses() {

		UserVo user = getCurrentUser();

		return expenseRepository.findAllByUser_Id(user.getId()).stream().map(this::convertExpenseToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> deleteExpense(Long id) {

		UserVo user = getCurrentUser();

		ExpenseVo expenseVo = getExpenseForCurrentUser(id, user.getId());

		expenseRepository.delete(expenseVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", "Expense deleted successfully");
		response.put("id", id);

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getExpenseSummary() {

		UserVo user = getCurrentUser();

		List<ExpenseVo> expenses = expenseRepository.findAllByUser_Id(user.getId());

		BigDecimal totalExpenses = expenses.stream().map(ExpenseVo::getAmount).filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, BigDecimal> categorySummary = expenses.stream().filter(e -> e.getAmount() != null)
				.collect(Collectors.groupingBy(ExpenseVo::getCategory,
						Collectors.reducing(BigDecimal.ZERO, ExpenseVo::getAmount, BigDecimal::add)));

		Map<String, Object> response = new HashMap<>();

		response.put("totalExpenses", totalExpenses);
		response.put("totalRecords", expenses.size());
		response.put("categorySummary", categorySummary);

		return response;
	}

	private expenseResponseDTO convertExpenseToResponse(ExpenseVo expenseVo) {

		expenseResponseDTO response = new expenseResponseDTO();

		response.setId(expenseVo.getId());
		response.setTitle(expenseVo.getTitle());
		response.setDescription(expenseVo.getDescription());
		response.setAmount(expenseVo.getAmount());
		response.setCategory(expenseVo.getCategory());
		response.setExpenseDate(expenseVo.getExpenseDate());
		response.setPaymentMethod(expenseVo.getPaymentMethod());
		response.setNotes(expenseVo.getNotes());

		return response;
	}

	// =====================================================
	// INCOME MODULE
	// =====================================================

	@Override
	public Map<String, Object> createUpdateIncome(Long id, incomeDTO income) {

		UserVo user = getCurrentUser();

		IncomeVo incomeVo;

		if (id != null) {

			// Update only the logged-in user's income
			incomeVo = getIncomeForCurrentUser(id, user.getId());

		} else {

			incomeVo = new IncomeVo();

			// Assign the logged-in user
			incomeVo.setUser(user);
		}

		incomeVo.setTitle(income.getTitle());
		incomeVo.setDescription(income.getDescription());
		incomeVo.setAmount(income.getAmount());
		incomeVo.setCategory(income.getCategory());
		incomeVo.setIncomeDate(income.getIncomeDate());
		incomeVo.setPaymentMethod(income.getPaymentMethod());
		incomeVo.setNotes(income.getNotes());

		incomeVo = incomeRepository.saveAndFlush(incomeVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", id == null ? "Income created successfully" : "Income updated successfully");

		response.put("incomeVO", convertIncomeToResponse(incomeVo));

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public incomeResponseDTO getIncomeById(Long id) {

		UserVo user = getCurrentUser();

		IncomeVo incomeVo = getIncomeForCurrentUser(id, user.getId());

		return convertIncomeToResponse(incomeVo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<incomeResponseDTO> getAllIncomes() {

		UserVo user = getCurrentUser();

		return incomeRepository.findAllByUser_Id(user.getId()).stream().map(this::convertIncomeToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> deleteIncome(Long id) {

		UserVo user = getCurrentUser();

		IncomeVo incomeVo = getIncomeForCurrentUser(id, user.getId());

		incomeRepository.delete(incomeVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", "Income deleted successfully");
		response.put("id", id);

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getIncomeSummary() {

		UserVo user = getCurrentUser();

		List<IncomeVo> incomes = incomeRepository.findAllByUser_Id(user.getId());

		BigDecimal totalIncome = incomes.stream().map(IncomeVo::getAmount).filter(amount -> amount != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, BigDecimal> categorySummary = incomes.stream().filter(i -> i.getAmount() != null)
				.collect(Collectors.groupingBy(IncomeVo::getCategory,
						Collectors.reducing(BigDecimal.ZERO, IncomeVo::getAmount, BigDecimal::add)));

		Map<String, Object> response = new HashMap<>();

		response.put("totalIncome", totalIncome);
		response.put("totalRecords", incomes.size());
		response.put("categorySummary", categorySummary);

		return response;
	}

	private incomeResponseDTO convertIncomeToResponse(IncomeVo incomeVo) {

		incomeResponseDTO response = new incomeResponseDTO();

		response.setId(incomeVo.getId());
		response.setTitle(incomeVo.getTitle());
		response.setDescription(incomeVo.getDescription());
		response.setAmount(incomeVo.getAmount());
		response.setCategory(incomeVo.getCategory());
		response.setIncomeDate(incomeVo.getIncomeDate());
		response.setPaymentMethod(incomeVo.getPaymentMethod());
		response.setNotes(incomeVo.getNotes());

		return response;
	}

	// =====================================================
	// BUDGET MODULE
	// =====================================================

	@Override
	public Map<String, Object> createUpdateBudget(Long id, budgetDTO budget) {

		UserVo user = getCurrentUser();

		BudgetVo budgetVo;

		if (id != null) {

			// Update only the logged-in user's budget
			budgetVo = getBudgetForCurrentUser(id, user.getId());

		} else {

			budgetVo = new BudgetVo();

			// Assign the logged-in user
			budgetVo.setUser(user);
		}

		// Check duplicate budget for this user only
		boolean duplicateBudget = budgetRepository.findAllByUser_Id(user.getId()).stream()
				.anyMatch(existing -> existing.getCategory().equalsIgnoreCase(budget.getCategory())
						&& existing.getMonth().equals(budget.getMonth()) && existing.getYear().equals(budget.getYear())
						&& !existing.getId().equals(id));

		if (duplicateBudget) {

			throw new RuntimeException("A budget already exists for this category and month");
		}

		budgetVo.setCategory(budget.getCategory());
		budgetVo.setMonthlyLimit(budget.getMonthlyLimit());
		budgetVo.setMonth(budget.getMonth());
		budgetVo.setYear(budget.getYear());
		budgetVo.setNotes(budget.getNotes());

		budgetVo = budgetRepository.saveAndFlush(budgetVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", id == null ? "Budget created successfully" : "Budget updated successfully");

		response.put("budgetVO", convertBudgetToResponse(budgetVo));

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getBudgetById(Long id) {

		UserVo user = getCurrentUser();

		BudgetVo budgetVo = getBudgetForCurrentUser(id, user.getId());

		return convertBudgetToResponse(budgetVo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAllBudgets(Integer month, Integer year) {

		UserVo user = getCurrentUser();

		return budgetRepository.findAllByUser_Id(user.getId()).stream()
				.filter(budget -> (month == null || budget.getMonth().equals(month))
						&& (year == null || budget.getYear().equals(year)))
				.map(this::convertBudgetToResponse).collect(Collectors.toList());
	}

	@Override
	public Map<String, Object> deleteBudget(Long id) {

		UserVo user = getCurrentUser();

		BudgetVo budgetVo = getBudgetForCurrentUser(id, user.getId());

		budgetRepository.delete(budgetVo);

		Map<String, Object> response = new HashMap<>();

		response.put("message", "Budget deleted successfully");
		response.put("id", id);

		return response;
	}

	private Map<String, Object> convertBudgetToResponse(BudgetVo budgetVo) {

		Map<String, Object> response = new HashMap<>();

		response.put("id", budgetVo.getId());
		response.put("category", budgetVo.getCategory());
		response.put("monthlyLimit", budgetVo.getMonthlyLimit());
		response.put("month", budgetVo.getMonth());
		response.put("year", budgetVo.getYear());
		response.put("notes", budgetVo.getNotes());

		return response;
	}

	// =====================================================
	// FINANCIAL DASHBOARD
	// =====================================================

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getFinanceSummary(Integer month, Integer year) {

		UserVo user = getCurrentUser();

		LocalDate today = LocalDate.now();

		int selectedMonth = month != null ? month : today.getMonthValue();

		int selectedYear = year != null ? year : today.getYear();

		if (selectedMonth < 1 || selectedMonth > 12) {
			throw new RuntimeException("Month must be between 1 and 12");
		}

		if (selectedYear < 2000 || selectedYear > 9999) {
			throw new RuntimeException("Invalid year");
		}

		LocalDate startDate = LocalDate.of(selectedYear, selectedMonth, 1);

		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		// Load only the authenticated user's records
		List<ExpenseVo> allExpenses = expenseRepository.findAllByUser_Id(user.getId());

		List<IncomeVo> allIncomes = incomeRepository.findAllByUser_Id(user.getId());

		// Monthly expenses
		List<ExpenseVo> monthlyExpenses = allExpenses.stream().filter(e -> e.getExpenseDate() != null
				&& !e.getExpenseDate().isBefore(startDate) && !e.getExpenseDate().isAfter(endDate))
				.collect(Collectors.toList());

		// Monthly incomes
		List<IncomeVo> monthlyIncomes = allIncomes.stream().filter(i -> i.getIncomeDate() != null
				&& !i.getIncomeDate().isBefore(startDate) && !i.getIncomeDate().isAfter(endDate))
				.collect(Collectors.toList());

		// Monthly totals
		BigDecimal totalIncome = sumIncome(monthlyIncomes);

		BigDecimal totalExpenses = sumExpenses(monthlyExpenses);

		BigDecimal balance = totalIncome.subtract(totalExpenses);

		// Savings and expense percentages
		BigDecimal savingsRate = BigDecimal.ZERO;

		BigDecimal expenseRate = BigDecimal.ZERO;

		if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

			savingsRate = balance.multiply(BigDecimal.valueOf(100)).divide(totalIncome, 2,
					java.math.RoundingMode.HALF_UP);

			expenseRate = totalExpenses.multiply(BigDecimal.valueOf(100)).divide(totalIncome, 2,
					java.math.RoundingMode.HALF_UP);
		}

		// All-time totals for this user
		BigDecimal allTimeIncome = sumIncome(allIncomes);

		BigDecimal allTimeExpenses = sumExpenses(allExpenses);

		// Income category summary
		Map<String, BigDecimal> incomeCategorySummary = monthlyIncomes.stream().filter(i -> i.getAmount() != null)
				.collect(Collectors.groupingBy(IncomeVo::getCategory,
						Collectors.reducing(BigDecimal.ZERO, IncomeVo::getAmount, BigDecimal::add)));

		// Expense category summary
		Map<String, BigDecimal> expenseCategorySummary = monthlyExpenses.stream().filter(e -> e.getAmount() != null)
				.collect(Collectors.groupingBy(ExpenseVo::getCategory,
						Collectors.reducing(BigDecimal.ZERO, ExpenseVo::getAmount, BigDecimal::add)));

		// Load only this user's budgets
		List<BudgetVo> monthlyBudgets = budgetRepository.findAllByUser_Id(user.getId()).stream()
				.filter(b -> b.getMonth().equals(selectedMonth) && b.getYear().equals(selectedYear))
				.collect(Collectors.toList());

		// Budget status calculation
		List<Map<String, Object>> budgetStatus = new ArrayList<>();

		for (BudgetVo budget : monthlyBudgets) {

			BigDecimal spent = monthlyExpenses.stream()
					.filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(budget.getCategory()))
					.map(ExpenseVo::getAmount).filter(amount -> amount != null)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);

			BigDecimal percentageUsed = BigDecimal.ZERO;

			if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {

				percentageUsed = spent.multiply(BigDecimal.valueOf(100)).divide(budget.getMonthlyLimit(), 2,
						java.math.RoundingMode.HALF_UP);
			}

			Map<String, Object> status = new HashMap<>();

			status.put("category", budget.getCategory());

			status.put("monthlyLimit", budget.getMonthlyLimit());

			status.put("spent", spent);

			status.put("remaining", remaining);

			status.put("percentageUsed", percentageUsed);

			if (percentageUsed.compareTo(BigDecimal.valueOf(100)) >= 0) {

				status.put("alert", "Budget exceeded");

			} else if (percentageUsed.compareTo(BigDecimal.valueOf(80)) >= 0) {

				status.put("alert", "Budget almost reached");

			} else {

				status.put("alert", "Within budget");
			}

			budgetStatus.add(status);
		}

		// Final dashboard response
		Map<String, Object> response = new HashMap<>();

		response.put("month", selectedMonth);
		response.put("year", selectedYear);

		response.put("totalIncome", totalIncome);
		response.put("totalExpenses", totalExpenses);
		response.put("availableBalance", balance);

		response.put("savingsRate", savingsRate);
		response.put("expenseRate", expenseRate);

		response.put("incomeRecords", monthlyIncomes.size());
		response.put("expenseRecords", monthlyExpenses.size());

		response.put("incomeCategorySummary", incomeCategorySummary);

		response.put("expenseCategorySummary", expenseCategorySummary);

		response.put("allTimeIncome", allTimeIncome);
		response.put("allTimeExpenses", allTimeExpenses);

		response.put("allTimeBalance", allTimeIncome.subtract(allTimeExpenses));

		response.put("budgetStatus", budgetStatus);

		return response;
	}

	// =====================================================
	// COMMON CALCULATION METHODS
	// =====================================================

	private BigDecimal sumIncome(List<IncomeVo> incomes) {

		return incomes.stream().map(IncomeVo::getAmount).filter(amount -> amount != null).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	private BigDecimal sumExpenses(List<ExpenseVo> expenses) {

		return expenses.stream().map(ExpenseVo::getAmount).filter(amount -> amount != null).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

}
