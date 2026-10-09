package dev.ruben.cookandshare.ingredient.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record IngredientDTORequest(
    @NotBlank @Size(max = 60) String name,
    String description,
    @NotBlank String imageUrl,
    @PositiveOrZero @Digits(integer = 5, fraction = 2) BigDecimal caloriesPer100g
) {
}
