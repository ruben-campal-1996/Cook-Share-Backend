package dev.ruben.cookandshare.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.recipe.entity.RecipeEntity;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
