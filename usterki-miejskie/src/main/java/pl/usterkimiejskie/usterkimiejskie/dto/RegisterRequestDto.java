package pl.usterkimiejskie.usterkimiejskie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Możesz dodać Lombok @Data lub @Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "Nazwa użytkownika nie może być pusta")
    @Size(min = 3, max = 50, message = "Nazwa użytkownika musi mieć od 3 do 50 znaków")
    private String username;

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Niepoprawny format adresu email")
    @Size(max = 100, message = "Email nie może być dłuższy niż 100 znaków")
    private String email;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 6, max = 100, message = "Hasło musi mieć od 6 do 100 znaków")
    private String password;

    // Gettery i Settery (lub Lombok)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}