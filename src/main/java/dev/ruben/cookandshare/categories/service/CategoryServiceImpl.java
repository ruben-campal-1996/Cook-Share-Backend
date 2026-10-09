package dev.ruben.cookandshare.categories.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.dtos.CategoryDTOResponse;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.categories.mappers.CategoryMapper;
import dev.ruben.cookandshare.categories.repository.CategoryRepository;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTOResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTOResponse findById(Integer id) {
        return categoryMapper.toResponse(getCategoryOrThrow(id));
    }

    @Override
    public CategoryDTOResponse create(CategoryDTORequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A category with this name already exists");
        }

        CategoryEntity category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryDTOResponse update(Integer id, CategoryDTORequest request) {
        CategoryEntity category = getCategoryOrThrow(id);

        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A category with this name already exists");
        }

        category.setName(request.name());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Integer id) {
        CategoryEntity category = getCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private CategoryEntity getCategoryOrThrow(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));
    }
}
