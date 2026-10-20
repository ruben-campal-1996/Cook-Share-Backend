package dev.ruben.cookandshare.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.infrastructure.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class CookandshareEndToEndTest {

    private static final String API = "/api/v1";
    private static final String CLIENT_EMAIL = "cliente@gmail.com";
    private static final String CLIENT_PASSWORD = "Cliente";
    private static final String TEST_JWT_KEY = Base64.getEncoder().encodeToString(new byte[64]);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add("jwt.key", () -> TEST_JWT_KEY);
    }

    @Test
    void categoryEndpointsSupportCreateReadUpdateAndDeleteAgainstPostgres() throws Exception {
        String name = "E2E " + UUID.randomUUID();
        String token = tokenFor(CLIENT_EMAIL, "USER");

        ResponseEntity<JsonNode> created = exchange(
                HttpMethod.POST, API + "/categories", Map.of("name", name), null);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        int id = created.getBody().get("id").asInt();
        assertEquals(name, created.getBody().get("name").asText());
        ResponseEntity<JsonNode> fetched = exchange(
                HttpMethod.GET, API + "/categories/" + id, null, token);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(name, fetched.getBody().get("name").asText());

        ResponseEntity<JsonNode> listed = exchange(HttpMethod.GET, API + "/categories", null, null);
        assertEquals(HttpStatus.OK, listed.getStatusCode());
        assertTrue(containsId(listed.getBody(), id));

        ResponseEntity<JsonNode> updated = exchange(
                HttpMethod.PUT, API + "/categories/" + id, Map.of("name", name + " updated"), token);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(name + " updated", updated.getBody().get("name").asText());

        ResponseEntity<JsonNode> deleted = exchange(
                HttpMethod.DELETE, API + "/categories/" + id, null, token);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                exchange(HttpMethod.GET, API + "/categories/" + id, null, token).getStatusCode());
    }

    @Test
    void ingredientEndpointsSupportCreateReadUpdateAndDeleteAgainstPostgres() throws Exception {
        String name = "E2E ingredient " + UUID.randomUUID();
        String token = tokenFor(CLIENT_EMAIL, "USER");
        Map<String, Object> payload = Map.of(
                "name", name,
                "description", "Created by end-to-end test",
                "imageUrl", "/images/test.png",
                "caloriesPer100g", 12.50);

        ResponseEntity<JsonNode> created = exchange(HttpMethod.POST, API + "/ingredients", payload, null);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        int id = created.getBody().get("id").asInt();
        assertEquals(name, created.getBody().get("name").asText());
        assertEquals(12.50, created.getBody().get("caloriesPer100g").asDouble());

        ResponseEntity<JsonNode> fetched = exchange(HttpMethod.GET, API + "/ingredients/" + id, null, null);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(name, fetched.getBody().get("name").asText());

        ResponseEntity<JsonNode> invalid = exchange(
                HttpMethod.POST, API + "/ingredients",
                Map.of("name", " ", "imageUrl", "/images/test.png"), token);
        assertEquals(HttpStatus.BAD_REQUEST, invalid.getStatusCode());

        ResponseEntity<JsonNode> updated = exchange(
                HttpMethod.PUT, API + "/ingredients/" + id,
                Map.of("name", name + " updated", "imageUrl", "/images/updated.png"), token);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(name + " updated", updated.getBody().get("name").asText());

        ResponseEntity<JsonNode> deleted = exchange(
                HttpMethod.DELETE, API + "/ingredients/" + id, null, token);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                exchange(HttpMethod.GET, API + "/ingredients/" + id, null, token).getStatusCode());
    }

    @Test
    void recipeEndpointsSupportCreateReadUpdateAndDeleteAgainstPostgres() throws Exception {
        String name = "E2E recipe " + UUID.randomUUID();
        String token = tokenFor(CLIENT_EMAIL, "USER");
        Map<String, Object> payload = recipePayload(name);

        ResponseEntity<JsonNode> created = exchange(HttpMethod.POST, API + "/recipes", payload, null);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        int id = created.getBody().get("id").asInt();
        assertEquals(name, created.getBody().get("name").asText());
        assertEquals(1, created.getBody().get("authorId").asInt());
        assertFalse(created.getBody().get("status").asBoolean());

        ResponseEntity<JsonNode> fetched = exchange(
                HttpMethod.GET, API + "/recipes/" + id, null, token);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals(name, fetched.getBody().get("name").asText());

        ResponseEntity<JsonNode> listed = exchange(HttpMethod.GET, API + "/recipes", null, null);
        assertEquals(HttpStatus.OK, listed.getStatusCode());
        assertTrue(containsId(listed.getBody(), id));

        ResponseEntity<JsonNode> anonymousDelete = exchange(
                HttpMethod.DELETE, API + "/recipes/" + id, null, null);
        assertEquals(HttpStatus.UNAUTHORIZED, anonymousDelete.getStatusCode());

        ResponseEntity<JsonNode> updated = exchange(
                HttpMethod.PUT, API + "/recipes/" + id, recipePayload(name + " updated"), token);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(name + " updated", updated.getBody().get("name").asText());

        ResponseEntity<JsonNode> deleted = exchange(
                HttpMethod.DELETE, API + "/recipes/" + id, null, token);
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                exchange(HttpMethod.GET, API + "/recipes/" + id, null, token).getStatusCode());
    }

    @Test
    void authenticationAndProfileEndpointsWorkOverHttp() throws Exception {
        ResponseEntity<JsonNode> login = exchange(
                HttpMethod.POST, API + "/auth/token",
                Map.of("email", CLIENT_EMAIL, "password", CLIENT_PASSWORD), null);
        assertEquals(HttpStatus.OK, login.getStatusCode());
        String token = login.getBody().get("token").asText();
        assertNotNull(token);
        assertFalse(token.isBlank());

        ResponseEntity<JsonNode> profile = exchange(HttpMethod.GET, API + "/user-details", null, token);
        assertEquals(HttpStatus.OK, profile.getStatusCode());
        String originalNickname = profile.getBody().get("nickname").asText();
        JsonNode avatar = profile.getBody().get("avatarUrl");
        String originalAvatarUrl = avatar == null || avatar.isNull() ? null : avatar.asText();
        String changedNickname = "e2e-" + UUID.randomUUID();

        ResponseEntity<JsonNode> updated = exchange(
                HttpMethod.PUT, API + "/user-details",
                Map.of("nickname", changedNickname, "avatarUrl", "/images/e2e.png"), token);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals(changedNickname, updated.getBody().get("nickname").asText());

        ResponseEntity<JsonNode> invalidLogin = exchange(
                HttpMethod.POST, API + "/auth/token",
                Map.of("email", CLIENT_EMAIL, "password", "incorrect"), null);
        assertEquals(HttpStatus.UNAUTHORIZED, invalidLogin.getStatusCode());

        ResponseEntity<JsonNode> restored = exchange(
                HttpMethod.PUT, API + "/user-details",
                profilePayload(originalNickname, originalAvatarUrl), token);
        assertEquals(HttpStatus.OK, restored.getStatusCode());

        UserAuthEntity client = userAuthRepository.findByEmail(CLIENT_EMAIL).orElseThrow();
        try {
            ResponseEntity<JsonNode> deactivated = exchange(
                    HttpMethod.DELETE, API + "/user-details", null, token);
            assertEquals(HttpStatus.NO_CONTENT, deactivated.getStatusCode());
            assertNotNull(userAuthRepository.findByEmail(CLIENT_EMAIL).orElseThrow().getDeletedAt());
        } finally {
            client.setDeletedAt(null);
            userAuthRepository.save(client);
        }
    }

    @Test
    void forcedPasswordChangeIssuesAWorkingTokenAndCanBeRestored() throws Exception {
        String email = "admin@gmail.com";
        UserAuthEntity admin = userAuthRepository.findByEmail(email).orElseThrow();
        String originalHash = admin.getPasswordHash();
        boolean originalChangeRequired = admin.isMustChangePassword();
        String newPassword = "E2e-new-pass-2026";

        try {
            ResponseEntity<JsonNode> loginRequiresPasswordChange = exchange(
                    HttpMethod.POST, API + "/auth/token",
                    Map.of("email", email, "password", "Admin"), null);
            assertEquals(HttpStatus.CONFLICT, loginRequiresPasswordChange.getStatusCode());

            ResponseEntity<JsonNode> changed = exchange(
                    HttpMethod.POST, API + "/auth/change-password",
                    Map.of("email", email, "currentPassword", "Admin", "newPassword", newPassword), null);
            assertEquals(HttpStatus.OK, changed.getStatusCode());
            assertNotNull(changed.getBody().get("token").asText());

            ResponseEntity<JsonNode> login = exchange(
                    HttpMethod.POST, API + "/auth/token",
                    Map.of("email", email, "password", newPassword), null);
            assertEquals(HttpStatus.OK, login.getStatusCode());
        } finally {
            admin.setPasswordHash(originalHash);
            admin.setMustChangePassword(originalChangeRequired);
            userAuthRepository.save(admin);
        }
    }

    private ResponseEntity<JsonNode> exchange(
            HttpMethod method, String path, Object body, String bearerToken) throws Exception {
        HttpRequest.BodyPublisher bodyPublisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body));
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method.name(), bodyPublisher);
        if (bearerToken != null) {
            request.header("Authorization", "Bearer " + bearerToken);
        }
        HttpResponse<String> response = HTTP_CLIENT.send(
                request.build(), HttpResponse.BodyHandlers.ofString());
        JsonNode responseBody = response.body().isBlank() ? null : OBJECT_MAPPER.readTree(response.body());
        return new ResponseEntity<>(responseBody, HttpStatus.valueOf(response.statusCode()));
    }

    private String tokenFor(String email, String role) {
        var now = java.time.Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(email)
                .expiresAt(now.plusSeconds(600))
                .claim("roles", java.util.List.of(role))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(), claims)).getTokenValue();
    }

    private static Map<String, Object> recipePayload(String name) {
        return Map.of(
                "name", name,
                "description", "End-to-end description",
                "instructions", "Prepare, cook, serve.",
                "image_url", "/images/e2e.png",
                "servings", 2,
                "author_id", 1,
                "total_calories", 500,
                "calories_per_serving", 250);
    }

    private static Map<String, Object> profilePayload(String nickname, String avatarUrl) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nickname", nickname);
        payload.put("avatarUrl", avatarUrl);
        return payload;
    }

    private static boolean containsId(JsonNode values, int id) {
        for (JsonNode value : values) {
            if (value.get("id").asInt() == id) {
                return true;
            }
        }
        return false;
    }
}
