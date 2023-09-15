package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventService eventService;

    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        return CategoryMapper.toDto(category);
    }

    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = CategoryMapper.toEntity(newCategoryDto);

        Category existingCategory = categoryRepository.findFirstByName(newCategoryDto.getName());
        if (existingCategory != null && !existingCategory.getId().equals(newCategory.getId())) {
            throw new ConflictException("Категория с таким именем уже существует.");
        }

        Category savedCategory = categoryRepository.save(newCategory);
        return CategoryMapper.toDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);

        if (Objects.isNull(category)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }

        boolean isCategoryLinked = eventService.findFirstByCategoryId(category.getId());
        if (isCategoryLinked) {
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElse(null);

        if (Objects.nonNull(category)) {

            Category newCategory = categoryRepository.findFirstByName(categoryDto.getName());
            if (newCategory != null && !newCategory.getId().equals(category.getId())) {
                throw new ConflictException("Category with this name already exists");
            }

            category.setName(categoryDto.getName());

            return CategoryMapper.toDto(categoryRepository.save(category));
        } else {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }
}
