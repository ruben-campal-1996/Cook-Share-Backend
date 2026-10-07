package dev.ruben.cookandshare.ingredient.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.ingredient.entity.IngredientEntity;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Integer>{
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
