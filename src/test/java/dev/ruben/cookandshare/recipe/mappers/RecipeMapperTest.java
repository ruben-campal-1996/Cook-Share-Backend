package dev.ruben.cookandshare.recipe.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import dev.ruben.cookandshare.categories.entity.CategoryEntity;
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
}
