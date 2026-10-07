package dev.ruben.cookandshare.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ruben.cookandshare.categories.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
