package dev.ruben.cookandshare.auth;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.UserDetailsDTORequest;
import dev.ruben.cookandshare.auth.dto.UserDetailsDTOResponse;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;

@Service
public class UserProfileService {

    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;

    public UserProfileService(
            UserAuthRepository userAuthRepository,
            UserDetailsRepository userDetailsRepository) {
        this.userAuthRepository = userAuthRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    public UserDetailsDTOResponse findByEmail(String email) {
        return toResponse(findDetailsByEmail(email));
    }

    public UserDetailsDTOResponse updateByEmail(String email, UserDetailsDTORequest request) {
        UserDetailsEntity details = findDetailsByEmail(email);
        if (userDetailsRepository.existsByNicknameAndUserIdNot(request.nickname(), details.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname is already in use");
        }

        details.setNickname(request.nickname());
        details.setAvatarUrl(request.avatarUrl());
        details.setUpdatedAt(LocalDateTime.now());

        return toResponse(userDetailsRepository.save(details));
    }

    public void deactivateByEmail(String email) {
        UserAuthEntity user = findActiveUserByEmail(email);
        user.setDeletedAt(LocalDateTime.now());
        userAuthRepository.save(user);
    }

    private UserDetailsEntity findDetailsByEmail(String email) {
        Integer userId = findActiveUserByEmail(email).getId();
        return userDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User details not found"));
    }

    private UserAuthEntity findActiveUserByEmail(String email) {
        UserAuthEntity user = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    private UserDetailsDTOResponse toResponse(UserDetailsEntity details) {
        return new UserDetailsDTOResponse(
                details.getUserId(),
                details.getNickname(),
                details.getAvatarUrl(),
                details.getRole().getId(),
                details.getRole().getName(),
                details.getUpdatedAt());
    }
}
