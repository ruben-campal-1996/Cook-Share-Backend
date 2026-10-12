package dev.ruben.cookandshare.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ruben.cookandshare.auth.dto.UserDetailsDTORequest;
import dev.ruben.cookandshare.auth.dto.UserDetailsDTOResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api-endpoint}/user-details")
public class UserDetailsController {

    private final UserProfileService userProfileService;

    public UserDetailsController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserDetailsDTOResponse> findCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.findByEmail(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<UserDetailsDTOResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserDetailsDTORequest request) {
        return ResponseEntity.ok(userProfileService.updateByEmail(authentication.getName(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentUser(Authentication authentication) {
        userProfileService.deactivateByEmail(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
