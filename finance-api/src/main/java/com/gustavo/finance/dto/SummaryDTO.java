package com.gustavo.finance.dto;

import java.math.BigDecimal;
import java.util.Map;

public class SummaryDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Integer totalTransactions;
    private Map<String, BigDecimal> expenseByCategory;
    private Map<String, BigDecimal> incomeByCategory;

    public SummaryDTO() {}

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public Map<String, BigDecimal> getExpenseByCategory() { return expenseByCategory; }
    public void setExpenseByCategory(Map<String, BigDecimal> expenseByCategory) { this.expenseByCategory = expenseByCategory; }

    public Map<String, BigDecimal> getIncomeByCategory() { return incomeByCategory; }
    public void setIncomeByCategory(Map<String, BigDecimal> incomeByCategory) { this.incomeByCategory = incomeByCategory; }
}
