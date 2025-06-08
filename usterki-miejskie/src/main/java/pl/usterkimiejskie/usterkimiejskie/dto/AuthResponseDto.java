package pl.usterkimiejskie.usterkimiejskie.dto;

// Możesz dodać Lombok @Data lub @Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken; // Opcjonalnie, jeśli implementujesz refresh tokeny
    private String tokenType = "Bearer";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Gettery i Settery (lub Lombok)
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}