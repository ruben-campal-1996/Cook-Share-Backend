package dev.ruben.cookandshare.categories.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDTORequest(
    @NotBlank @Size (max = 50) String name) {
}
