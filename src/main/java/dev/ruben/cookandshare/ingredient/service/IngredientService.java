package dev.ruben.cookandshare.ingredient.service;

import java.util.List;

import dev.ruben.cookandshare.ingredient.dtos.IngredientDTORequest;
import dev.ruben.cookandshare.ingredient.dtos.IngredientDTOResponse;

public interface IngredientService {
    List<IngredientDTOResponse> findAll();
    
    IngredientDTOResponse findById(Integer id);
    IngredientDTOResponse create(IngredientDTORequest request);
    IngredientDTOResponse update(Integer id, IngredientDTORequest request);
    void delete(Integer id);
}
