package dev.ruben.cookandshare.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.RegisterRequest;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.entity.UserRoleEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;
import dev.ruben.cookandshare.auth.repository.UserRoleRepository;

class UserRegistrationServiceTest {

    private final UserAuthRepository userAuthRepository = mock(UserAuthRepository.class);
    private final UserDetailsRepository userDetailsRepository = mock(UserDetailsRepository.class);
    private final UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRegistrationService service = new UserRegistrationService(
            userAuthRepository, userDetailsRepository, userRoleRepository, passwordEncoder);
    private final RegisterRequest request =
            new RegisterRequest("cook@example.com", "secret123", "Cocinero");

    @BeforeEach
    void setUp() {
        when(userRoleRepository.findByName("User")).thenReturn(Optional.of(new UserRoleEntity(1, "User")));
        when(userAuthRepository.save(any(UserAuthEntity.class))).thenAnswer(invocation -> {
            UserAuthEntity user = invocation.getArgument(0);
            user.setId(42);
            return user;
        });
    }

    @Test
    void registersAnAccountWithAnEncodedPasswordAndDefaultUserRole() {
        service.register(request);

        var userCaptor = org.mockito.ArgumentCaptor.forClass(UserAuthEntity.class);
        verify(userAuthRepository).save(userCaptor.capture());
        UserAuthEntity savedUser = userCaptor.getValue();
        assertEquals("cook@example.com", savedUser.getEmail());
        assertFalse(savedUser.isMustChangePassword());
        assertNotEquals("secret123", savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("secret123", savedUser.getPasswordHash()));

        var detailsCaptor = org.mockito.ArgumentCaptor.forClass(UserDetailsEntity.class);
        verify(userDetailsRepository).save(detailsCaptor.capture());
        UserDetailsEntity savedDetails = detailsCaptor.getValue();
        assertEquals(42, savedDetails.getUserId());
        assertEquals("Cocinero", savedDetails.getNickname());
        assertEquals("./images/avatar_defaultUrl.png", savedDetails.getAvatarUrl());
        assertEquals("User", savedDetails.getRole().getName());
    }

    @Test
    void rejectsAnEmailThatIsAlreadyInUse() {
        when(userAuthRepository.existsByEmail(request.email())).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> service.register(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userAuthRepository, never()).save(any());
        verify(userDetailsRepository, never()).save(any());
    }

    @Test
    void rejectsANicknameThatIsAlreadyInUse() {
        when(userDetailsRepository.existsByNickname(request.nickname())).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> service.register(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userAuthRepository, never()).save(any());
        verify(userDetailsRepository, never()).save(any());
    }
}
