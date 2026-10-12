package dev.ruben.cookandshare.recipe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;
import dev.ruben.cookandshare.categories.entity.CategoryEntity;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTOResponse;
import dev.ruben.cookandshare.recipe.entity.RecipeEntity;
import dev.ruben.cookandshare.recipe.mappers.RecipeMapper;
import dev.ruben.cookandshare.recipe.repository.RecipeRepository;
import dev.ruben.cookandshare.recipe.status.entity.RecipeStatusEntity;
import dev.ruben.cookandshare.recipe.status.repository.RecipeStatusRepository;

class RecipeServiceImplTest {

    private final RecipeRepository recipeRepository = mock(RecipeRepository.class);
    private final UserDetailsRepository userDetailsRepository = mock(UserDetailsRepository.class);
    private final RecipeStatusRepository recipeStatusRepository = mock(RecipeStatusRepository.class);
    private final RecipeMapper recipeMapper = mock(RecipeMapper.class);
    private final RecipeServiceImpl service = new RecipeServiceImpl(
            recipeRepository, userDetailsRepository, recipeStatusRepository, recipeMapper);

    @Test
    void createSetsAuthorPendingStatusAndUnpublishedState() {
        RecipeDTORequest request = request("Soup", 12);
        RecipeEntity mapped = recipe("Soup");
        RecipeEntity saved = recipe("Soup");
        UserDetailsEntity author = user(12);
        RecipeStatusEntity pending = status(2, "PENDING");
        RecipeDTOResponse response = response(6, "Soup");
        when(recipeRepository.existsByName("Soup")).thenReturn(false);
        when(recipeMapper.toEntity(request)).thenReturn(mapped);
        when(userDetailsRepository.findById(12)).thenReturn(Optional.of(author));
        when(recipeStatusRepository.findByName("PENDING")).thenReturn(Optional.of(pending));
        when(recipeRepository.save(mapped)).thenReturn(saved);
        when(recipeMapper.toResponse(saved)).thenReturn(response);

        assertEquals(response, service.create(request));
        assertSame(author, mapped.getAuthor());
        assertSame(pending, mapped.getRecipeStatus());
        assertFalse(mapped.isStatus());
        verify(recipeRepository).save(mapped);
    }

    @Test
    void createReturnsConflictForDuplicateName() {
        when(recipeRepository.existsByName("Soup")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.create(request("Soup", 12)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(recipeMapper, userDetailsRepository, recipeStatusRepository);
    }

    @Test
    void createReturnsNotFoundWhenAuthorDoesNotExist() {
        RecipeDTORequest request = request("Soup", 12);
        when(recipeRepository.existsByName("Soup")).thenReturn(false);
        when(recipeMapper.toEntity(request)).thenReturn(recipe("Soup"));
        when(userDetailsRepository.findById(12)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createReturnsServerErrorWhenPendingStatusIsNotConfigured() {
        RecipeDTORequest request = request("Soup", 12);
        when(recipeRepository.existsByName("Soup")).thenReturn(false);
        when(recipeMapper.toEntity(request)).thenReturn(recipe("Soup"));
        when(userDetailsRepository.findById(12)).thenReturn(Optional.of(user(12)));
        when(recipeStatusRepository.findByName("PENDING")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.create(request));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }

    @Test
    void updatePreservesWorkflowFieldsAndCategories() {
        RecipeEntity existing = recipe("Old");
        existing.setId(4);
        existing.setAuthor(user(8));
        existing.setRecipeStatus(status(3, "ACCEPTED"));
        existing.setStatus(true);
        existing.setCategories(Set.of(new CategoryEntity(7, "Dinner")));
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime publishedAt = LocalDateTime.of(2025, 1, 2, 10, 0);
        LocalDateTime reviewedAt = LocalDateTime.of(2025, 1, 3, 10, 0);
        UserDetailsEntity reviewer = user(9);
        existing.setCreatedAt(createdAt);
        existing.setPublishedAt(publishedAt);
        existing.setReviewedAt(reviewedAt);
        existing.setReviewer(reviewer);

        RecipeDTORequest request = request("Updated", 12);
        RecipeEntity mapped = recipe("Updated");
        UserDetailsEntity newAuthor = user(12);
        RecipeDTOResponse response = response(4, "Updated");
        when(recipeRepository.findById(4)).thenReturn(Optional.of(existing));
        when(recipeRepository.existsByNameAndIdNot("Updated", 4)).thenReturn(false);
        when(recipeMapper.toEntity(request)).thenReturn(mapped);
        when(userDetailsRepository.findById(12)).thenReturn(Optional.of(newAuthor));
        when(recipeRepository.save(mapped)).thenReturn(mapped);
        when(recipeMapper.toResponse(mapped)).thenReturn(response);

        assertEquals(response, service.update(4, request));
        assertEquals(4, mapped.getId());
        assertSame(newAuthor, mapped.getAuthor());
        assertSame(existing.getRecipeStatus(), mapped.getRecipeStatus());
        assertSame(existing.getCategories(), mapped.getCategories());
        assertEquals(createdAt, mapped.getCreatedAt());
        assertEquals(publishedAt, mapped.getPublishedAt());
        assertEquals(reviewedAt, mapped.getReviewedAt());
        assertSame(reviewer, mapped.getReviewer());
        assertEquals(existing.isStatus(), mapped.isStatus());
    }

    @Test
    void updateReturnsNotFoundWhenRecipeDoesNotExist() {
        when(recipeRepository.findById(4)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.update(4, request("Updated", 12)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(recipeMapper, userDetailsRepository);
    }

    @Test
    void updateReturnsConflictWhenNameBelongsToAnotherRecipe() {
        when(recipeRepository.findById(4)).thenReturn(Optional.of(recipe("Old")));
        when(recipeRepository.existsByNameAndIdNot("Taken", 4)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.update(4, request("Taken", 12)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(recipeMapper, userDetailsRepository);
    }

    @Test
    void deleteRemovesExistingRecipe() {
        RecipeEntity entity = recipe("Soup");
        when(recipeRepository.findById(4)).thenReturn(Optional.of(entity));

        service.delete(4);

        verify(recipeRepository).delete(entity);
    }

    private static RecipeDTORequest request(String name, Integer authorId) {
        return new RecipeDTORequest(name, "Description", "Cook it", "/recipe.png",
                2, authorId, 400, 200);
    }

    private static RecipeDTOResponse response(Integer id, String name) {
        return new RecipeDTOResponse(id, name, "Description", "Cook it", "/recipe.png",
                2, java.util.List.of(), 12, 2, 400, 200,
                null, null, false, null, null, null);
    }

    private static RecipeEntity recipe(String name) {
        RecipeEntity entity = new RecipeEntity();
        entity.setName(name);
        entity.setDescription("Description");
        entity.setInstructions("Cook it");
        entity.setImageUrl("/recipe.png");
        entity.setServings(2);
        return entity;
    }

    private static UserDetailsEntity user(Integer id) {
        UserDetailsEntity user = new UserDetailsEntity();
        user.setUserId(id);
        return user;
    }

    private static RecipeStatusEntity status(Integer id, String name) {
        RecipeStatusEntity status = new RecipeStatusEntity();
        status.setId(id);
        status.setName(name);
        return status;
    }
}
