package dev.ruben.cookandshare.auth;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ruben.cookandshare.auth.dto.RegisterRequest;
import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.entity.UserRoleEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;
import dev.ruben.cookandshare.auth.repository.UserRoleRepository;

@Service
public class UserRegistrationService {

    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(
            UserAuthRepository userAuthRepository,
            UserDetailsRepository userDetailsRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        this.userAuthRepository = userAuthRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userAuthRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }
        if (userDetailsRepository.existsByNickname(request.nickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname is already in use");
        }

        UserRoleEntity userRole = userRoleRepository.findByName("User")
                .orElseThrow(() -> new IllegalStateException("Default User role is not configured"));
        UserAuthEntity user = new UserAuthEntity(
                null,
                request.email(),
                passwordEncoder.encode(request.password()),
                false,
                LocalDateTime.now(),
                null);
        user = userAuthRepository.save(user);

        UserDetailsEntity details = new UserDetailsEntity();
        details.setUserId(user.getId());
        details.setNickname(request.nickname());
        details.setAvatarUrl("./images/avatar_defaultUrl.png");
        details.setRole(userRole);
        userDetailsRepository.save(details);
    }
}
