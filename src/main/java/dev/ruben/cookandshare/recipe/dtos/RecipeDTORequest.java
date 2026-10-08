package dev.ruben.cookandshare.recipe.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecipeDTORequest(
    @NotBlank @Size (max=155) String name,
    String description,
    String instructions,
    @NotBlank String imageUrl,
    @NotNull Integer servings,
    @NotNull Integer authorId,
    @NotNull Integer statusId,
    Integer totalCalories,
    Integer caloriesPerServing,
    @NotNull boolean status,
    LocalDateTime publishedAt,
    LocalDateTime reviewedAt,
    Integer reviewedBy
) {
}
