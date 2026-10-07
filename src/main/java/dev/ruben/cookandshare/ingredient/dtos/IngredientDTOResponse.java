package dev.ruben.cookandshare.ingredient.dtos;

public record IngredientDTOResponse(
    Integer id,
    String name,
    String description,
    String imageUrl,
    Integer caloriesPer100g
) {
}
