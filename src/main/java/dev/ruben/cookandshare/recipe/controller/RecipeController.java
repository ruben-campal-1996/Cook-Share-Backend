package dev.ruben.cookandshare.recipe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ruben.cookandshare.recipe.dtos.RecipeDTORequest;
import dev.ruben.cookandshare.recipe.dtos.RecipeDTOResponse;
import dev.ruben.cookandshare.recipe.service.RecipeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api-endpoint}/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTOResponse>> findAll() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTOResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RecipeDTOResponse> create(@Valid @RequestBody RecipeDTORequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTOResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody RecipeDTORequest request) {
        return ResponseEntity.ok(recipeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
