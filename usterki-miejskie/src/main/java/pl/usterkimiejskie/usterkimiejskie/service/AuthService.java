package pl.usterkimiejskie.usterkimiejskie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.usterkimiejskie.usterkimiejskie.dto.AuthResponseDto;
import pl.usterkimiejskie.usterkimiejskie.dto.LoginRequestDto;
import pl.usterkimiejskie.usterkimiejskie.dto.RegisterRequestDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Role;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.exception.UserAlreadyExistsException; // Stworzymy ten wyjątek
import pl.usterkimiejskie.usterkimiejskie.repository.RoleRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;

import java.util.Set;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Do procesu logowania

    @Autowired
    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User registerUser(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new UserAlreadyExistsException("Użytkownik o nazwie '" + registerRequestDto.getUsername() + "' już istnieje.");
        }
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Użytkownik z adresem email '" + registerRequestDto.getEmail() + "' już istnieje.");
        }

        User newUser = new User();
        newUser.setUsername(registerRequestDto.getUsername());
        newUser.setEmail(registerRequestDto.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        newUser.setEnabled(true); // Domyślnie aktywuj konto

        // Domyślnie przypisz rolę ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Błąd: Rola ROLE_USER nie została znaleziona w bazie.")); // Powinna być z migracji
        newUser.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(newUser);
        logger.info("Zarejestrowano nowego użytkownika: {}", savedUser.getUsername());
        return savedUser;
    }

    public AuthResponseDto loginUser(LoginRequestDto loginRequestDto) {
        try {
            // Próba uwierzytelnienia użytkownika za pomocą AuthenticationManager
            // To wywoła CustomUserDetailsService.loadUserByUsername() i sprawdzi hasło
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.warn("Nieudana próba logowania dla użytkownika: {}", loginRequestDto.getUsername(), e);
            throw e; // Rzuć wyjątek dalej, aby GlobalExceptionHandler mógł go obsłużyć (np. jako 401 Unauthorized)
        }

        // Jeśli uwierzytelnienie się powiodło, pobierz UserDetails i wygeneruj token
        // CustomUserDetailsService powinien być wywołany przez AuthenticationManager
        var user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Błąd wewnętrzny: Użytkownik znaleziony podczas uwierzytelniania, ale nie w repozytorium."));

        String jwtToken = jwtService.generateToken(user); // Używamy encji User, bo implementuje UserDetails (pośrednio)
        // lub bezpośrednio UserDetails zwrócone przez loadUserByUsername
        // String refreshToken = jwtService.generateRefreshToken(user); // Opcjonalnie

        logger.info("Użytkownik {} pomyślnie zalogowany. Wygenerowano token.", user.getUsername());
        return new AuthResponseDto(jwtToken); // Na razie tylko access token
        // return new AuthResponseDto(jwtToken, refreshToken); // Jeśli używasz refresh tokenów
    }
}