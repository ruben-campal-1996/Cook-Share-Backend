package dev.ruben.cookandshare.config;

import jakarta.servlet.DispatcherType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import dev.ruben.cookandshare.auth.entity.UserAuthEntity;
import dev.ruben.cookandshare.auth.entity.UserDetailsEntity;
import dev.ruben.cookandshare.auth.repository.UserAuthRepository;
import dev.ruben.cookandshare.auth.repository.UserDetailsRepository;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {
    @Value("${jwt.key}")
    private String key;

    @Value("${api-endpoint}")
    private String endpoint;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                endpoint + "/auth/token",
                                endpoint + "/auth/change-password",
                                endpoint + "/categories",
                                endpoint + "/recipes",
                                endpoint + "/ingredients")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                endpoint + "/categories",
                                endpoint + "/categories/*",
                                endpoint + "/ingredients",
                                endpoint + "/ingredients/*",
                                endpoint + "/recipes",
                                endpoint + "/images",
                                endpoint + "/images/*",
                                endpoint + "/recipes/*",
                                endpoint + "/user-details")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT,
                                endpoint + "/categories/*",
                                endpoint + "/recipes/*",
                                endpoint + "/ingredients/*",
                                endpoint + "/user-details")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                endpoint + "/categories/*",
                                endpoint + "/recipes/*",
                                endpoint + "/ingredients/*",
                                endpoint + "/user-details")
                        .authenticated()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService(
                        UserAuthRepository userAuthRepository,
                        UserDetailsRepository userDetailsRepository) {
                return email -> {
                        UserAuthEntity user = userAuthRepository.findByEmail(email)
                                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                        UserDetailsEntity details = userDetailsRepository.findByUserId(user.getId())
                                        .orElseThrow(() -> new BadCredentialsException("User has no assigned role"));
                        List<SimpleGrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority("ROLE_" + details.getRole().getName()));

                        return User.withUsername(user.getEmail())
                                        .password(user.getPasswordHash())
                                        .authorities(authorities)
                                        .disabled(user.getDeletedAt() != null)
                                        .build();
                };
        }

        @Bean
        public AuthenticationProvider authenticationProvider(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
                authoritiesConverter.setAuthoritiesClaimName("roles");
                authoritiesConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
                return converter;
        }

        @Bean
        public JwtEncoder jwtEncoder() {
                return new NimbusJwtEncoder(new ImmutableSecret<>(key.getBytes(StandardCharsets.UTF_8)));
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                SecretKeySpec secretKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA512");
                return NimbusJwtDecoder.withSecretKey(secretKey)
                                .macAlgorithm(MacAlgorithm.HS512)
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfiguration() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin"));
                configuration.setExposedHeaders(Arrays.asList(HttpHeaders.CONTENT_DISPOSITION));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
