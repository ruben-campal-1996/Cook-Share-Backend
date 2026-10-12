package dev.ruben.cookandshare.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDetailsDTORequest(
        @NotBlank @Size(max = 60) String nickname,
        @Size(max = 2048) String avatarUrl) {
}
