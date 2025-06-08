package pl.usterkimiejskie.usterkimiejskie.dto;

import jakarta.validation.constraints.NotBlank;

// Możesz dodać Lombok @Data lub @Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Nazwa użytkownika nie może być pusta")
    private String username;

    @NotBlank(message = "Hasło nie może być puste")
    private String password;

    // Gettery i Settery (lub Lombok)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}