package dev.ruben.cookandshare.recipe.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTOResponse;
import dev.ruben.cookandshare.recipe.entity.RecipeEntity;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "recipeStatus", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    RecipeEntity toEntity(RecipeDTORequest dto);

    @Mapping(target = "authorId", source = "author.userId")
    @Mapping(target = "statusId", source = "recipeStatus.id")
    @Mapping(target = "reviewedBy", source = "reviewer.userId")
    RecipeDTOResponse toResponse(RecipeEntity entity);
}