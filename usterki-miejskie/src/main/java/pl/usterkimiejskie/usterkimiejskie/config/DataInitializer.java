package pl.usterkimiejskie.usterkimiejskie.config;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;

import java.util.List;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final UsterkaRepository usterkaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, UsterkaRepository usterkaRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.usterkaRepository = usterkaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User maciej = new User("Maciej", "maciej@example.com", passwordEncoder.encode("test"), "USER");
            User admin = new User("Admin", "admin@example.com", passwordEncoder.encode("admin"), "ADMIN");
            userRepository.saveAll(List.of(maciej, admin));

            Usterka u1 = new Usterka("Zepsute światło uliczne", "Oczekuje", "Legnica, Słowiańska 5", null, 51.2098, 16.1615, maciej);
            Usterka u2 = new Usterka("Dziura w chodniku", "W trakcie", "Legnica, Wrocławska 7", null, 51.2105, 16.1622, maciej);
            Usterka u3 = new Usterka("Nieświecące przejście", "Naprawione", "Legnica, Piastowska 10", null, 51.2110, 16.1630, maciej);

            usterkaRepository.saveAll(List.of(u1, u2, u3));
        }
    }
}
