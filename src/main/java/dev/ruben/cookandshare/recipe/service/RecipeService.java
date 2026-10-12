package dev.ruben.cookandshare.recipe.service;

import java.util.List;

import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTOResponse;

public interface RecipeService {
    List<RecipeDTOResponse> findAll();

    RecipeDTOResponse findById(Integer id);

    RecipeDTOResponse create(RecipeDTORequest request);

    RecipeDTOResponse update(Integer id, RecipeDTORequest request);

    void delete(Integer id);
}
