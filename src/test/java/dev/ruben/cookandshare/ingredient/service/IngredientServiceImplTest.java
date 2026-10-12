package dev.ruben.cookandshare.ingredient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.ingredient.dtos.IngredientDTORequest;
import dev.ruben.cookandshare.ingredient.dtos.IngredientDTOResponse;
import dev.ruben.cookandshare.ingredient.entity.IngredientEntity;
import dev.ruben.cookandshare.ingredient.mappers.IngredientMapper;
import dev.ruben.cookandshare.ingredient.repository.IngredientRepository;

class IngredientServiceImplTest {

    private final IngredientRepository repository = mock(IngredientRepository.class);
    private final IngredientMapper mapper = mock(IngredientMapper.class);
    private final IngredientServiceImpl service = new IngredientServiceImpl(repository, mapper);

    @Test
    void findAllMapsEveryIngredient() {
        IngredientEntity entity = ingredient(1, "Tomato");
        IngredientDTOResponse response = response(1, "Tomato");
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        assertEquals(List.of(response), service.findAll());
        verify(mapper).toResponse(entity);
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() {
        when(repository.findById(5)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.findById(5));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void createSavesAndMapsANewIngredient() {
        IngredientDTORequest request = request("Basil");
        IngredientEntity entity = ingredient(null, "Basil");
        IngredientEntity saved = ingredient(9, "Basil");
        IngredientDTOResponse response = response(9, "Basil");
        when(repository.existsByName("Basil")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        assertEquals(response, service.create(request));
        verify(repository).save(entity);
    }

    @Test
    void createReturnsConflictForDuplicateName() {
        when(repository.existsByName("Basil")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.create(request("Basil")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void updatePreservesTheExistingId() {
        IngredientEntity existing = ingredient(5, "Old");
        IngredientEntity mapped = ingredient(null, "New");
        IngredientDTORequest request = request("New");
        IngredientDTOResponse response = response(5, "New");
        when(repository.findById(5)).thenReturn(Optional.of(existing));
        when(repository.existsByNameAndIdNot("New", 5)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(mapped);
        when(mapper.toResponse(mapped)).thenReturn(response);

        assertEquals(response, service.update(5, request));
        assertEquals(5, mapped.getId());
        verify(repository).save(mapped);
    }

    @Test
    void updateReturnsConflictForDuplicateName() {
        when(repository.findById(5)).thenReturn(Optional.of(ingredient(5, "Old")));
        when(repository.existsByNameAndIdNot("Taken", 5)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.update(5, request("Taken")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(mapper);
    }

    @Test
    void deleteRemovesTheExistingIngredient() {
        IngredientEntity entity = ingredient(5, "Tomato");
        when(repository.findById(5)).thenReturn(Optional.of(entity));

        service.delete(5);

        verify(repository).delete(entity);
    }

    private static IngredientDTORequest request(String name) {
        return new IngredientDTORequest(name, "Fresh", "/image.png", new BigDecimal("20.5"));
    }

    private static IngredientDTOResponse response(Integer id, String name) {
        return new IngredientDTOResponse(id, name, "Fresh", "/image.png", new BigDecimal("20.5"));
    }

    private static IngredientEntity ingredient(Integer id, String name) {
        return new IngredientEntity(id, name, "Fresh", "/image.png", new BigDecimal("20.5"));
    }
}
