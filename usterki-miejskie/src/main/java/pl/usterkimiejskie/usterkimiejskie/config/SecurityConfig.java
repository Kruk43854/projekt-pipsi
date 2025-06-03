package pl.usterkimiejskie.usterkimiejskie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Dla wyłączenia CSRF
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Zezwól na dostęp do WSZYSTKICH endpointów bez uwierzytelnienia
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                )
                // Wyłącz CSRF całkowicie, jeśli nie używasz formularzy HTML serwowanych przez Spring
                .csrf(AbstractHttpConfigurer::disable)
                // Wyłącz HTTP Basic, aby nie pojawiało się okno logowania
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}