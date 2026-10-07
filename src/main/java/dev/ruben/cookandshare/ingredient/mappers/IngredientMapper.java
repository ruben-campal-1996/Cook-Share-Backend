package dev.ruben.cookandshare.ingredient.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.ruben.cookandshare.ingredient.dtos.IngredientDTORequest;
import dev.ruben.cookandshare.ingredient.dtos.IngredientDTOResponse;
import dev.ruben.cookandshare.ingredient.entity.IngredientEntity;

@Mapper (componentModel = "spring")
public interface IngredientMapper {
    IngredientDTOResponse toResponse(IngredientEntity ingredient);

    @Mapping (target="id", ignore=true)
    IngredientEntity toEntity(IngredientDTORequest request);
}
