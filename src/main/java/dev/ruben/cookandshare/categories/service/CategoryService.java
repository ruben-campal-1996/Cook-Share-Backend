package dev.ruben.cookandshare.categories.service;

import java.util.List;

import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.dtos.CategoryDTOResponse;

public interface CategoryService {
    List<CategoryDTOResponse> findAll();

    CategoryDTOResponse findById(Integer id);
    CategoryDTOResponse create(CategoryDTORequest request);
    CategoryDTOResponse update(Integer id, CategoryDTORequest request);
    void delete(Integer id);
}
