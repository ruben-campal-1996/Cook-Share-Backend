package dev.ruben.cookandshare.categories.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.categories.repository.CategoryRepository;
import dev.ruben.cookandshare.infrastructure.IntegrationTest;

class CategoryServicePostgresIntegrationTest extends IntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @MockitoSpyBean
    private CategoryRepository categoryRepository;

    @Test
    void createPersistsCategoryAndReturnsItFromPostgres() {
        String name = "Mockito " + UUID.randomUUID();

        var created = categoryService.create(new CategoryDTORequest(name));

        assertEquals(name, created.name());
        assertEquals(created, categoryService.findById(created.id()));
        verify(categoryRepository).save(any(CategoryEntity.class));

        categoryService.delete(created.id());
    }
}
