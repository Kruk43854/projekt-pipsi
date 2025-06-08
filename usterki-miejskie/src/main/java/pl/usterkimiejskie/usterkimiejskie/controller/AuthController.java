package pl.usterkimiejskie.usterkimiejskie.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.usterkimiejskie.usterkimiejskie.dto.AuthResponseDto;
import pl.usterkimiejskie.usterkimiejskie.dto.LoginRequestDto;
import pl.usterkimiejskie.usterkimiejskie.dto.RegisterRequestDto;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.service.AuthService;

@RestController
@RequestMapping("/api/auth") // Bazowy URL dla endpointów uwierzytelniania
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        // Walidacja RegisterRequestDto jest obsługiwana przez @Valid
        // UserAlreadyExistsException będzie obsłużony przez @ResponseStatus lub GlobalExceptionHandler
        User registeredUser = authService.registerUser(registerRequestDto);
        // Możesz zwrócić tylko status 201 lub dane użytkownika (bez hasła!)
        return ResponseEntity.status(HttpStatus.CREATED).body("Użytkownik zarejestrowany pomyślnie: " + registeredUser.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        // Walidacja LoginRequestDto
        try {
            AuthResponseDto authResponse = authService.loginUser(loginRequestDto);
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            // GlobalExceptionHandler może to obsłużyć, ale tutaj możemy dać bardziej specyficzny komunikat
            // lub po prostu pozwolić GlobalExceptionHandlerowi na obsługę (co zwróci 500, jeśli nie ma dedykowanego handlera dla AuthenticationException)
            // Lepszym podejściem jest dedykowany handler w GlobalExceptionHandler dla AuthenticationException zwracający 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błąd logowania: Nieprawidłowa nazwa użytkownika lub hasło.");
        }
    }
}