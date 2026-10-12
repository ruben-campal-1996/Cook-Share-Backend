package dev.ruben.cookandshare.auth.dto;

import java.time.LocalDateTime;

public record UserDetailsDTOResponse(
        Integer userId,
        String nickname,
        String avatarUrl,
        Integer roleId,
        String roleName,
        LocalDateTime updatedAt) {
}
