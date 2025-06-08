package pl.usterkimiejskie.usterkimiejskie.config; // Upewnij się, że pakiet jest poprawny

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // Upewnij się, że masz ten import i zależność security
import pl.usterkimiejskie.usterkimiejskie.entity.Role;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.RoleRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initTestUser(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder // Wstrzykujemy PasswordEncoder
    ) {
        return args -> {
            // Nazwa użytkownika, którego potrzebujemy w UsterkaController
            String testUsername = "testuser";
            String testUserEmail = "jakisemail@gmail.com";
            String testUserPassword = "haslo"; // Hasło, które zahaszujemy

            // 1. Upewnij się, że rola ROLE_USER istnieje (powinna być z migracji V1)
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        // Jeśli rola nie istnieje, a powinna być z migracji, to jest problem.
                        // Dla bezpieczeństwa można ją tu stworzyć, ale lepiej, żeby migracja działała.
                        System.err.println("OSTRZEŻENIE: Rola ROLE_USER nie została znaleziona, tworzenie...");
                        return roleRepository.save(new Role("ROLE_USER"));
                    });

            // 2. Sprawdź, czy użytkownik "testuser" już istnieje
            if (userRepository.findByUsername(testUsername).isEmpty()) {
                // Jeśli nie istnieje, utwórz go
                User testUser = new User();
                testUser.setUsername(testUsername);
                testUser.setEmail(testUserEmail);
                // Użyj PasswordEncoder do zahaszowania hasła
                testUser.setPasswordHash(passwordEncoder.encode(testUserPassword));
                testUser.setEnabled(true);
                testUser.setRoles(Set.of(userRole)); // Przypisz rolę ROLE_USER

                userRepository.save(testUser);
                System.out.println("INFO: Utworzono użytkownika testowego: " + testUsername + " z rolą ROLE_USER");
            } else {
                System.out.println("INFO: Użytkownik testowy '" + testUsername + "' już istnieje.");
            }
        };
    }
}