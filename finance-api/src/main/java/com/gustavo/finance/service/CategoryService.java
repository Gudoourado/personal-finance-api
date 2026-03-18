package com.gustavo.finance.service;

import com.gustavo.finance.dto.CategoryDTO;
import com.gustavo.finance.exception.DuplicateResourceException;
import com.gustavo.finance.exception.ResourceNotFoundException;
import com.gustavo.finance.model.Category;
import com.gustavo.finance.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));
        return toDTO(category);
    }

    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Já existe uma categoria com o nome: " + dto.getName());
        }
        Category category = new Category(dto.getName(), dto.getDescription());
        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));

        categoryRepository.findByNameIgnoreCase(dto.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Já existe uma categoria com o nome: " + dto.getName());
                    }
                });

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        Category updated = categoryRepository.save(category);
        return toDTO(updated);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));

        if (!category.getTransactions().isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível excluir categoria com transações associadas. " +
                    "Remova as transações primeiro.");
        }
        categoryRepository.delete(category);
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setTotalTransactions(category.getTransactions() != null ? category.getTransactions().size() : 0);
        return dto;
    }
}
