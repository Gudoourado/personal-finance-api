package com.gustavo.finance.service;

import com.gustavo.finance.dto.SummaryDTO;
import com.gustavo.finance.dto.TransactionDTO;
import com.gustavo.finance.exception.ResourceNotFoundException;
import com.gustavo.finance.model.Category;
import com.gustavo.finance.model.Transaction;
import com.gustavo.finance.model.TransactionType;
import com.gustavo.finance.repository.CategoryRepository;
import com.gustavo.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                               CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAllByOrderByDateDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com id: " + id));
        return toDTO(transaction);
    }

    public TransactionDTO create(TransactionDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada com id: " + dto.getCategoryId()));

        Transaction transaction = new Transaction();
        transaction.setDescription(dto.getDescription());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setCategory(category);
        transaction.setNotes(dto.getNotes());

        Transaction saved = transactionRepository.save(transaction);
        return toDTO(saved);
    }

    public TransactionDTO update(Long id, TransactionDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada com id: " + dto.getCategoryId()));

        transaction.setDescription(dto.getDescription());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setCategory(category);
        transaction.setNotes(dto.getNotes());

        Transaction updated = transactionRepository.save(transaction);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transação não encontrada com id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    public List<TransactionDTO> findByType(TransactionType type) {
        return transactionRepository.findByType(type)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoria não encontrada com id: " + categoryId);
        }
        return transactionRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> search(String keyword) {
        return transactionRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SummaryDTO getSummary(LocalDate startDate, LocalDate endDate) {
        SummaryDTO summary = new SummaryDTO();

        BigDecimal totalIncome;
        BigDecimal totalExpense;
        List<Transaction> transactions;

        if (startDate != null && endDate != null) {
            totalIncome = transactionRepository.sumByTypeAndDateBetween(
                    TransactionType.RECEITA, startDate, endDate);
            totalExpense = transactionRepository.sumByTypeAndDateBetween(
                    TransactionType.DESPESA, startDate, endDate);
            transactions = transactionRepository.findByDateBetween(startDate, endDate);
        } else {
            totalIncome = transactionRepository.sumByType(TransactionType.RECEITA);
            totalExpense = transactionRepository.sumByType(TransactionType.DESPESA);
            transactions = transactionRepository.findAll();
        }

        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setBalance(totalIncome.subtract(totalExpense));
        summary.setTotalTransactions(transactions.size());

        // Gastos por categoria
        List<Object[]> expenseByCategory = (startDate != null && endDate != null)
                ? transactionRepository.sumByCategoryAndTypeAndDateBetween(
                        TransactionType.DESPESA, startDate, endDate)
                : transactionRepository.sumByCategoryAndType(TransactionType.DESPESA);

        Map<String, BigDecimal> expenseMap = new LinkedHashMap<>();
        for (Object[] row : expenseByCategory) {
            expenseMap.put((String) row[0], (BigDecimal) row[1]);
        }
        summary.setExpenseByCategory(expenseMap);

        // Receitas por categoria
        List<Object[]> incomeByCategory = (startDate != null && endDate != null)
                ? transactionRepository.sumByCategoryAndTypeAndDateBetween(
                        TransactionType.RECEITA, startDate, endDate)
                : transactionRepository.sumByCategoryAndType(TransactionType.RECEITA);

        Map<String, BigDecimal> incomeMap = new LinkedHashMap<>();
        for (Object[] row : incomeByCategory) {
            incomeMap.put((String) row[0], (BigDecimal) row[1]);
        }
        summary.setIncomeByCategory(incomeMap);

        return summary;
    }

    private TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setDate(transaction.getDate());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setCategoryName(transaction.getCategory().getName());
        dto.setNotes(transaction.getNotes());
        return dto;
    }
}
