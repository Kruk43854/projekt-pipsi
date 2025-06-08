package pl.usterkimiejskie.usterkimiejskie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // Import dla SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import dla filtra
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Opcjonalnie: Włącza adnotacje @PreAuthorize, @PostAuthorize itp.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService; // To będzie nasz CustomUserDetailsService
    private final PasswordEncoder passwordEncoder;     // Z AppConfig

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Wyłączamy CSRF, typowe dla API bezstanowych z JWT
                .authorizeHttpRequests(auth -> auth
                        // Endpointy publiczne (nie wymagają uwierzytelnienia)
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/api/auth/**"), // Rejestracja, logowanie
                                AntPathRequestMatcher.antMatcher("/h2-console/**"), // Konsola H2 (tylko dla developmentu!)
                                AntPathRequestMatcher.antMatcher("/swagger-ui/**"), // Dokumentacja API (jeśli dodasz)
                                AntPathRequestMatcher.antMatcher("/v3/api-docs/**") // Dokumentacja API (jeśli dodasz)
                        ).permitAll()

                        // Endpointy do odczytu usterek mogą być publiczne
                        .requestMatchers(HttpMethod.GET, "/api/usterki", "/api/usterki/**").permitAll()

                        // Przykład: Zgłaszanie usterek wymaga roli USER lub ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/usterki").hasAnyRole("USER", "ADMIN")
                        // Przykład: Aktualizacja i usuwanie usterek tylko dla ADMINA
                        .requestMatchers(HttpMethod.PUT, "/api/usterki/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usterki/**").hasRole("ADMIN")

                        .anyRequest().authenticated() // Wszystkie inne żądania wymagają uwierzytelnienia
                )
                // Konfiguracja zarządzania sesją: STATELESS - nie tworzymy ani nie używamy sesji HTTP
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Konfiguracja dostawcy uwierzytelniania
                .authenticationProvider(authenticationProvider())
                // Dodajemy nasz filtr JWT przed standardowym filtrem UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Dla konsoli H2, jeśli używasz ramek
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    // Bean dostawcy uwierzytelniania
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Ustawiamy nasz UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder);     // Ustawiamy nasz PasswordEncoder
        return authProvider;
    }

    // Bean menedżera uwierzytelniania - potrzebny do endpointu logowania
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}