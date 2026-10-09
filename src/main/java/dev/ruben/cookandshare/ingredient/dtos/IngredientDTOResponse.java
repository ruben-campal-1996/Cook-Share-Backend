package dev.ruben.cookandshare.ingredient.dtos;

import java.math.BigDecimal;

public record IngredientDTOResponse(
    Integer id,
    String name,
    String description,
    String imageUrl,
    BigDecimal caloriesPer100g
) {
}
