package dev.ruben.cookandshare.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.ChangePasswordRequest;
import dev.ruben.cookandshare.auth.dto.LoginRequest;
import dev.ruben.cookandshare.auth.dto.TokenResponse;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            TokenService tokenService,
            AuthenticationManager authenticationManager,
            UserAuthRepository userAuthRepository,
            PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/token")
    public TokenResponse token(@Valid @RequestBody LoginRequest request) {
        UserAuthEntity user = findUser(request.email());

        if (user.isMustChangePassword()) {
            if (user.getDeletedAt() != null
                    || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password change required");
        }

        Authentication authentication = authenticate(request.email(), request.password());
        return new TokenResponse(tokenService.generateToken(authentication));
    }

    @PostMapping("/change-password")
    public TokenResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserAuthEntity user = findUser(request.email());

        if (!user.isMustChangePassword()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Password change is not required");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        userAuthRepository.save(user);

        Authentication authentication = authenticate(request.email(), request.newPassword());
        return new TokenResponse(tokenService.generateToken(authentication));
    }

    private Authentication authenticate(String email, String password) {
        try {
            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, password));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    private UserAuthEntity findUser(String email) {
        return userAuthRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }
}
