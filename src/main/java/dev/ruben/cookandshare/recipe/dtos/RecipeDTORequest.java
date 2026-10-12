package dev.ruben.cookandshare.recipe.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeDTORequest(
    @NotBlank @Size(max = 155) String name,
    String description,
    String instructions,
    @JsonProperty("image_url")
    String imageUrl,
    @NotNull @Positive Integer servings,
    @JsonProperty("author_id")
    @NotNull @Positive Integer authorId,
    @JsonProperty("total_calories")
    @Positive Integer totalCalories,
    @JsonProperty("calories_per_serving")
    @Positive Integer caloriesPerServing
) {
}
