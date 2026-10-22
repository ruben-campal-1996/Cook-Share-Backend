package dev.ruben.cookandshare.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank @Size(max = 60) String nickname) {
}
