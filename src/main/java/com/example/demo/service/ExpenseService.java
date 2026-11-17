package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.budgetDTO;
import com.example.demo.dto.expenseDTO;
import com.example.demo.dto.incomeDTO;
import com.example.demo.response.incomeResponseDTO;
import com.example.demo.response.expenseResponseDTO;

public interface ExpenseService {

    // ================= EXPENSE =================

    Map<String, Object> createUpdateExpense(Long id, expenseDTO expense);

    expenseResponseDTO getExpenseById(Long id);

    List<expenseResponseDTO> getAllExpenses();

    Map<String, Object> deleteExpense(Long id);

    Map<String, Object> getExpenseSummary();


    // ================= INCOME =================

    Map<String, Object> createUpdateIncome(Long id, incomeDTO income);

    incomeResponseDTO getIncomeById(Long id);

    List<incomeResponseDTO> getAllIncomes();

    Map<String, Object> deleteIncome(Long id);

    Map<String, Object> getIncomeSummary();


    // ================= BUDGET =================

    Map<String, Object> createUpdateBudget(Long id, budgetDTO budget);

    Map<String, Object> getBudgetById(Long id);

    List<Map<String, Object>> getAllBudgets(Integer month, Integer year);

    Map<String, Object> deleteBudget(Long id);


    // ================= FINANCIAL DASHBOARD =================

    Map<String, Object> getFinanceSummary(Integer month, Integer year);

}