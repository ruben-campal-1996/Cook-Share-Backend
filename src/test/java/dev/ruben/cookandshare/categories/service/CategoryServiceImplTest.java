package dev.ruben.cookandshare.categories.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.categories.dtos.CategoryDTORequest;
import dev.ruben.cookandshare.categories.dtos.CategoryDTOResponse;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.categories.mappers.CategoryMapper;
import dev.ruben.cookandshare.categories.repository.CategoryRepository;

class CategoryServiceImplTest {

    private final CategoryRepository repository = mock(CategoryRepository.class);
    private final CategoryMapper mapper = mock(CategoryMapper.class);
    private final CategoryServiceImpl service = new CategoryServiceImpl(repository, mapper);

    @Test
    void findAllMapsEveryCategory() {
        CategoryEntity entity = new CategoryEntity(1, "Vegetarian");
        CategoryDTOResponse response = new CategoryDTOResponse(1, "Vegetarian");
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        assertEquals(List.of(response), service.findAll());
        verify(mapper).toResponse(entity);
    }

    @Test
    void findByIdMapsTheRequestedCategory() {
        CategoryEntity entity = new CategoryEntity(4, "Dinner");
        CategoryDTOResponse response = new CategoryDTOResponse(4, "Dinner");
        when(repository.findById(4)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        assertEquals(response, service.findById(4));
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() {
        when(repository.findById(4)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.findById(4));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void createSavesAndMapsANewCategory() {
        CategoryDTORequest request = new CategoryDTORequest("Dessert");
        CategoryEntity entity = new CategoryEntity(null, "Dessert");
        CategoryEntity saved = new CategoryEntity(8, "Dessert");
        CategoryDTOResponse response = new CategoryDTOResponse(8, "Dessert");
        when(repository.existsByName("Dessert")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        assertEquals(response, service.create(request));
        verify(repository).save(entity);
    }

    @Test
    void createReturnsConflictForDuplicateName() {
        when(repository.existsByName("Dessert")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(new CategoryDTORequest("Dessert")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void updateChangesTheExistingCategory() {
        CategoryEntity existing = new CategoryEntity(3, "Old");
        CategoryEntity updated = new CategoryEntity(3, "New");
        CategoryDTOResponse response = new CategoryDTOResponse(3, "New");
        when(repository.findById(3)).thenReturn(Optional.of(existing));
        when(repository.existsByNameAndIdNot("New", 3)).thenReturn(false);
        when(repository.save(existing)).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(response);

        assertEquals(response, service.update(3, new CategoryDTORequest("New")));
        assertEquals("New", existing.getName());
        verify(repository).save(existing);
    }

    @Test
    void updateReturnsConflictForAnotherCategoryWithSameName() {
        when(repository.findById(3)).thenReturn(Optional.of(new CategoryEntity(3, "Old")));
        when(repository.existsByNameAndIdNot("Used", 3)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.update(3, new CategoryDTORequest("Used")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void deleteRemovesTheExistingCategory() {
        CategoryEntity entity = new CategoryEntity(3, "Dinner");
        when(repository.findById(3)).thenReturn(Optional.of(entity));

        service.delete(3);

        verify(repository).delete(entity);
    }
}
