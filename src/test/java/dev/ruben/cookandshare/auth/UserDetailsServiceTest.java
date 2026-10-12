package dev.ruben.cookandshare.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.UserDetailsDTORequest;
import dev.ruben.cookandshare.auth.dto.UserDetailsDTOResponse;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.entity.UserRoleEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;

class UserDetailsServiceTest {

    private static final String EMAIL = "cliente@gmail.com";

    private final UserAuthRepository userAuthRepository = mock(UserAuthRepository.class);
    private final UserDetailsRepository userDetailsRepository = mock(UserDetailsRepository.class);
    private final UserProfileService service = new UserProfileService(userAuthRepository, userDetailsRepository);

    @Test
    void findByEmailReturnsProfileForAuthenticatedUser() {
        LocalDateTime updatedAt = LocalDateTime.now();
        UserAuthEntity user = new UserAuthEntity(7, EMAIL, "hash", false, updatedAt, null);
        UserDetailsEntity details = new UserDetailsEntity();
        details.setUserId(7);
        details.setNickname("cocinero");
        details.setAvatarUrl("/images/avatar.png");
        details.setRole(new UserRoleEntity(2, "USER"));
        details.setUpdatedAt(updatedAt);

        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsRepository.findByUserId(7)).thenReturn(Optional.of(details));

        UserDetailsDTOResponse response = service.findByEmail(EMAIL);

        assertEquals(7, response.userId());
        assertEquals("cocinero", response.nickname());
        assertEquals("/images/avatar.png", response.avatarUrl());
        assertEquals(2, response.roleId());
        assertEquals("USER", response.roleName());
        assertEquals(updatedAt, response.updatedAt());
    }

    @Test
    void findByEmailReturnsNotFoundWhenProfileDoesNotExist() {
        UserAuthEntity user = new UserAuthEntity(7, EMAIL, "hash", false, LocalDateTime.now(), null);
        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsRepository.findByUserId(7)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByEmail(EMAIL));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateByEmailChangesNicknameAndAvatarButPreservesRole() {
        UserAuthEntity user = new UserAuthEntity(7, EMAIL, "hash", false, LocalDateTime.now(), null);
        UserDetailsEntity details = new UserDetailsEntity();
        details.setUserId(7);
        details.setNickname("old-name");
        details.setRole(new UserRoleEntity(2, "User"));
        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsRepository.findByUserId(7)).thenReturn(Optional.of(details));
        when(userDetailsRepository.existsByNicknameAndUserIdNot("new-name", 7)).thenReturn(false);
        when(userDetailsRepository.save(details)).thenReturn(details);

        service.updateByEmail(EMAIL, new UserDetailsDTORequest("new-name", "/avatar.png"));

        assertEquals("new-name", details.getNickname());
        assertEquals("/avatar.png", details.getAvatarUrl());
        assertEquals("User", details.getRole().getName());
        verify(userDetailsRepository).save(details);
    }

    @Test
    void updateByEmailRejectsNicknameUsedByAnotherUser() {
        UserAuthEntity user = new UserAuthEntity(7, EMAIL, "hash", false, LocalDateTime.now(), null);
        UserDetailsEntity details = new UserDetailsEntity();
        details.setUserId(7);
        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userDetailsRepository.findByUserId(7)).thenReturn(Optional.of(details));
        when(userDetailsRepository.existsByNicknameAndUserIdNot("taken", 7)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateByEmail(EMAIL,
                        new UserDetailsDTORequest("taken", null)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void deactivateByEmailSoftDeletesAccount() {
        UserAuthEntity user = new UserAuthEntity(7, EMAIL, "hash", false, LocalDateTime.now(), null);
        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        service.deactivateByEmail(EMAIL);

        assertNotNull(user.getDeletedAt());
        verify(userAuthRepository).save(user);
    }
}
