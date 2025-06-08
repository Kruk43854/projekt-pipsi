package pl.usterkimiejskie.usterkimiejskie.config; // Lub np. pl.usterkimiejskie.usterkimiejskie.security

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.usterkimiejskie.usterkimiejskie.service.JwtService; // Twój JwtService

import java.io.IOException;

@Component // Oznaczamy jako komponent Springa, aby można go było wstrzyknąć
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Rozszerzamy OncePerRequestFilter, aby filtr wykonał się tylko raz na żądanie

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Wstrzykniemy nasz CustomUserDetailsService

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Pobierz nagłówek Authorization
        final String jwt;
        final String username;

        // Jeśli nie ma nagłówka Authorization lub nie zaczyna się od "Bearer ", przekaż żądanie dalej
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Wyodrębnij token (pomijając "Bearer ")
        try {
            username = jwtService.extractUsername(jwt); // Wyodrębnij nazwę użytkownika z tokenu

            // Jeśli nazwa użytkownika została wyodrębniona i użytkownik nie jest jeszcze uwierzytelniony w bieżącym kontekście
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // Załaduj dane użytkownika

                // Jeśli token jest ważny
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Utwórz obiekt uwierzytelnienia
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Hasło nie jest potrzebne, bo już sprawdziliśmy token
                            userDetails.getAuthorities() // Role użytkownika
                    );
                    // Ustaw dodatkowe szczegóły uwierzytelnienia (np. adres IP, sesja - choć tu sesji nie ma)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // Ustaw obiekt uwierzytelnienia w kontekście bezpieczeństwa Springa
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Użytkownik '" + username + "' pomyślnie uwierzytelniony przez JWT.");
                } else {
                    logger.warn("Token JWT jest nieważny dla użytkownika: " + username);
                }
            }
        } catch (Exception e) {
            // Tutaj można obsłużyć wyjątki związane z parsowaniem/walidacją tokenu, np. ExpiredJwtException, MalformedJwtException
            // Na razie logujemy i pozwalamy żądaniu przejść dalej (co prawdopodobnie spowoduje błąd 401/403 później, jeśli zasób jest chroniony)
            logger.warn("Błąd podczas walidacji tokenu JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response); // Przekaż żądanie do następnego filtra w łańcuchu
    }
}