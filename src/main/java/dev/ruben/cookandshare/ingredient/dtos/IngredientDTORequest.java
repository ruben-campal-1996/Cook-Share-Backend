package dev.ruben.cookandshare.ingredient.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record IngredientDTORequest(
    @NotBlank @Size(max = 60) String name,
    String description,
    @NotBlank String imageUrl,
    @PositiveOrZero Integer caloriesPer100g
) {
}
