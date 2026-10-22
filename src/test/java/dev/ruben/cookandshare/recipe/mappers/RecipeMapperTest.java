package dev.ruben.cookandshare.recipe.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.entity.RecipeEntity;

class RecipeMapperTest {

    private final RecipeMapper recipeMapper = Mappers.getMapper(RecipeMapper.class);

    @Test
    void toResponseIncludesSortedCategoryIds() {
        RecipeEntity recipe = new RecipeEntity();
        recipe.setCategories(Set.of(
                new CategoryEntity(7, "Dinner"),
                new CategoryEntity(2, "Easy")));

        assertEquals(List.of(2, 7), recipeMapper.toResponse(recipe).categoryIds());
    }

    @Test
    void toEntityMapsRequestFieldsUsingBuilder() {
        RecipeDTORequest request = new RecipeDTORequest(
                "Soup", "Description", "Instructions", "image.png", 4, 12, 500, 125);

        RecipeEntity recipe = recipeMapper.toEntity(request);

        assertEquals("Soup", recipe.getName());
        assertEquals("Description", recipe.getDescription());
        assertEquals("Instructions", recipe.getInstructions());
        assertEquals("image.png", recipe.getImageUrl());
        assertEquals(4, recipe.getServings());
        assertEquals(500, recipe.getTotalCalories());
        assertEquals(125, recipe.getCaloriesPerServing());
        assertEquals(Set.of(), recipe.getCategories());
        assertTrue(recipe.isStatus());
    }
}
