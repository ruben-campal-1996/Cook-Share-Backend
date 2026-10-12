package dev.ruben.cookandshare.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.LoginRequest;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;

class AuthControllerTest {

    private static final String EMAIL = "cliente@gmail.com";
    private static final String PASSWORD = "Cliente";

    private final TokenService tokenService = mock(TokenService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final UserAuthRepository userAuthRepository = mock(UserAuthRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthController controller = new AuthController(
            tokenService, authenticationManager, userAuthRepository, passwordEncoder);

    @BeforeEach
    void setUp() {
        UserAuthEntity user = new UserAuthEntity(
                1, EMAIL, passwordEncoder.encode(PASSWORD), true, LocalDateTime.now(), null);
        when(userAuthRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    @Test
    void tokenReturnsConflictWhenPasswordIsValidAndChangeIsRequired() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.token(new LoginRequest(EMAIL, PASSWORD)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(authenticationManager, never()).authenticate(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void tokenReturnsUnauthorizedWhenPasswordIsInvalidAndChangeIsRequired() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.token(new LoginRequest(EMAIL, "wrong-password")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(authenticationManager, never()).authenticate(org.mockito.ArgumentMatchers.any());
    }
}
