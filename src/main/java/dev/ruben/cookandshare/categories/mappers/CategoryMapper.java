package dev.ruben.cookandshare.categories.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.ruben.cookandshare.categories.dtos.CategoryDTOReponse;
import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;

@Mapper (componentModel = "spring")
public interface CategoryMapper {
    CategoryDTOReponse toResponse(CategoryEntity category);

    @Mapping (target = "id", ignore = true)
    CategoryEntity toEntity(CategoryDTORequest request);
}
