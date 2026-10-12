package dev.ruben.cookandshare.recipe.status.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.recipe.status.entity.RecipeStatusEntity;

public interface RecipeStatusRepository extends JpaRepository<RecipeStatusEntity, Integer> {
    Optional<RecipeStatusEntity> findByName(String name);
}
