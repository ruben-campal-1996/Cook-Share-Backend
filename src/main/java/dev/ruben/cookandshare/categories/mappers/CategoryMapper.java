package dev.ruben.cookandshare.categories.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.ruben.cookandshare.categories.dtos.CategoryDTOResponse;
import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;

@Mapper (componentModel = "spring")
public interface CategoryMapper {
    CategoryDTOResponse toResponse(CategoryEntity category);

    @Mapping (target = "id", ignore = true)
    CategoryEntity toEntity(CategoryDTORequest request);
}
