package dev.ruben.cookandshare.recipe.dtos;

import java.time.LocalDateTime;

public record RecipeDTOResponse(
    Integer id,
    String name,
    String description,
    String instructions,
    String imageUrl,
    Integer servings,
    Integer authorId,
    Integer statusId,
    Integer totalCalories,
    Integer caloriesPerServing,
    LocalDateTime publishedAt,
    LocalDateTime reviewedAt,
    Integer reviewedBy
) {
    
}
