package dev.ruben.cookandshare.ingredient.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.ingredient.dtos.IngredientDTORequest;
import dev.ruben.cookandshare.ingredient.dtos.IngredientDTOResponse;
import dev.ruben.cookandshare.ingredient.entity.IngredientEntity;
import dev.ruben.cookandshare.ingredient.mappers.IngredientMapper;
import dev.ruben.cookandshare.ingredient.repository.IngredientRepository;

@Service
@Transactional
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(
            IngredientRepository ingredientRepository,
            IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDTOResponse> findAll() {
        return ingredientRepository.findAll().stream()
                .map(ingredientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientDTOResponse findById(Integer id) {
        return ingredientMapper.toResponse(getIngredientOrThrow(id));
    }

    @Override
    public IngredientDTOResponse create(IngredientDTORequest request) {
        if (ingredientRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An ingredient with this name already exists");
        }

        IngredientEntity ingredient = ingredientMapper.toEntity(request);
        return ingredientMapper.toResponse(ingredientRepository.save(ingredient));
    }

    @Override
    public IngredientDTOResponse update(Integer id, IngredientDTORequest request) {
        IngredientEntity ingredient = getIngredientOrThrow(id);

        if (ingredientRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An ingredient with this name already exists");
        }

        IngredientEntity updatedIngredient = ingredientMapper.toEntity(request);
        updatedIngredient.setId(ingredient.getId());
        return ingredientMapper.toResponse(ingredientRepository.save(updatedIngredient));
    }

    @Override
    public void delete(Integer id) {
        IngredientEntity ingredient = getIngredientOrThrow(id);
        ingredientRepository.delete(ingredient);
    }

    private IngredientEntity getIngredientOrThrow(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ingredient not found"));
    }
}
