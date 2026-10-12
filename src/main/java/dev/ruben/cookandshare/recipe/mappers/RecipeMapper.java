package dev.ruben.cookandshare.recipe.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.ruben.cookandshare.categories.entity.CategoryEntity;
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
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    RecipeEntity toEntity(RecipeDTORequest dto);

    @Mapping(target = "authorId", source = "author.userId")
    @Mapping(target = "statusId", source = "recipeStatus.id")
    @Mapping(target = "categoryIds", source = "categories")
    @Mapping(target = "reviewedBy", source = "reviewer.userId")
    RecipeDTOResponse toResponse(RecipeEntity entity);

    default List<Integer> toCategoryIds(Set<CategoryEntity> categories) {
        return categories.stream()
                .map(CategoryEntity::getId)
                .sorted()
                .toList();
    }
}