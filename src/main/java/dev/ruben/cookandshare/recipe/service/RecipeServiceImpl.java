package dev.ruben.cookandshare.recipe.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTOResponse;
import dev.ruben.cookandshare.recipe.entity.RecipeEntity;
import dev.ruben.cookandshare.recipe.mappers.RecipeMapper;
import dev.ruben.cookandshare.recipe.repository.RecipeRepository;
import dev.ruben.cookandshare.recipe.status.entity.RecipeStatusEntity;
import dev.ruben.cookandshare.recipe.status.repository.RecipeStatusRepository;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {

    private static final String PENDING_STATUS = "PENDING";

    private final RecipeRepository recipeRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final RecipeStatusRepository recipeStatusRepository;
    private final RecipeMapper recipeMapper;

    public RecipeServiceImpl(
            RecipeRepository recipeRepository,
            UserDetailsRepository userDetailsRepository,
            RecipeStatusRepository recipeStatusRepository,
            RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.recipeStatusRepository = recipeStatusRepository;
        this.recipeMapper = recipeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDTOResponse> findAll() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDTOResponse findById(Integer id) {
        return recipeMapper.toResponse(getRecipeOrThrow(id));
    }

    @Override
    public RecipeDTOResponse create(RecipeDTORequest request) {
        if (recipeRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A recipe with this name already exists");
        }

        RecipeEntity recipe = recipeMapper.toEntity(request);
        recipe.setAuthor(getAuthorOrThrow(request.authorId()));
        recipe.setRecipeStatus(getPendingStatusOrThrow());
        recipe.setStatus(false);

        return recipeMapper.toResponse(recipeRepository.save(recipe));
    }

    @Override
    public RecipeDTOResponse update(Integer id, RecipeDTORequest request) {
        RecipeEntity recipe = getRecipeOrThrow(id);

        if (recipeRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A recipe with this name already exists");
        }

        RecipeEntity updatedRecipe = recipeMapper.toEntity(request);
        updatedRecipe.setId(recipe.getId());
        updatedRecipe.setAuthor(getAuthorOrThrow(request.authorId()));
        updatedRecipe.setRecipeStatus(recipe.getRecipeStatus());
        updatedRecipe.setCreatedAt(recipe.getCreatedAt());
        updatedRecipe.setPublishedAt(recipe.getPublishedAt());
        updatedRecipe.setReviewedAt(recipe.getReviewedAt());
        updatedRecipe.setReviewer(recipe.getReviewer());
        updatedRecipe.setStatus(recipe.isStatus());

        return recipeMapper.toResponse(recipeRepository.save(updatedRecipe));
    }

    @Override
    public void delete(Integer id) {
        recipeRepository.delete(getRecipeOrThrow(id));
    }

    private RecipeEntity getRecipeOrThrow(Integer id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found"));
    }

    private UserDetailsEntity getAuthorOrThrow(Integer authorId) {
        return userDetailsRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe author not found"));
    }

    private RecipeStatusEntity getPendingStatusOrThrow() {
        return recipeStatusRepository.findByName(PENDING_STATUS)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Pending recipe status is not configured"));
    }
}
