package dev.ruben.cookandshare.ingredient.controller;

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

import dev.ruben.cookandshare.ingredient.dtos.IngredientDTORequest;
import dev.ruben.cookandshare.ingredient.dtos.IngredientDTOResponse;
import dev.ruben.cookandshare.ingredient.service.IngredientService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping ("${api-endpoint}/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }
 
    @GetMapping
    public ResponseEntity<List<IngredientDTOResponse>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTOResponse> findById(@PathVariable Integer id){
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDTOResponse> create(@Valid @RequestBody IngredientDTORequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTOResponse> update(
        @PathVariable Integer id,
        @Valid @RequestBody IngredientDTORequest request) {
            return ResponseEntity.ok(ingredientService.update(id, request));
        } 

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
