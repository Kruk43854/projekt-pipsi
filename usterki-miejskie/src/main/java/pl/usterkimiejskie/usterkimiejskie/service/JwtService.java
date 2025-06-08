package pl.usterkimiejskie.usterkimiejskie.service; // Lub np. pl.usterkimiejskie.usterkimiejskie.security

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Wczytaj sekretny klucz i czas ważności tokenu z application.properties
    // WAŻNE: Sekretny klucz powinien być długi, losowy i przechowywany bezpiecznie!
    // Dla developmentu możemy go zahardkodować lub dać domyślną wartość.
    // W produkcji powinien być w zmiennej środowiskowej lub innym bezpiecznym miejscu.
    @Value("${application.security.jwt.secret-key: fallbackSecretKeyIfNotDefinedInPropertiesFallbackSecretKeyIfNotDefinedInProperties}") // Przykładowy, długi klucz
    private String secretKeyString;

    @Value("${application.security.jwt.expiration}") // np. 86400000 (24 godziny w milisekundach)
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}") // np. 604800000 (7 dni w milisekundach)
    private long refreshExpiration;


    // Metoda do ekstrakcji nazwy użytkownika z tokenu
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generyczna metoda do ekstrakcji konkretnego "claim" (informacji) z tokenu
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generowanie tokenu JWT dla danego użytkownika (UserDetails)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails); // Bez dodatkowych claims
    }

    // Generowanie tokenu JWT z dodatkowymi "claims"
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    // Generowanie tokenu odświeżającego (refresh token)
    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims) // Dodatkowe informacje w tokenie
                .subject(userDetails.getUsername()) // Główny podmiot tokenu (nazwa użytkownika)
                .issuedAt(new Date(System.currentTimeMillis())) // Data wydania tokenu
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Data ważności tokenu
                .signWith(getSignInKey()) // Podpisanie tokenu kluczem
                .compact(); // Zbudowanie i serializacja tokenu do stringa
    }


    // Sprawdzenie, czy token jest ważny (nazwa użytkownika pasuje i token nie wygasł)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Sprawdzenie, czy token wygasł
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Ekstrakcja daty ważności z tokenu
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Ekstrakcja wszystkich "claims" z tokenu
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Weryfikacja podpisu
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Pobranie klucza do podpisywania/weryfikacji tokenów
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}