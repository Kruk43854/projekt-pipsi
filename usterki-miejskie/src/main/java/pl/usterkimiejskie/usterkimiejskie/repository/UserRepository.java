package pl.usterkimiejskie.usterkimiejskie.repository; // Upewnij się, że nazwa pakietu jest poprawna

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.usterkimiejskie.usterkimiejskie.entity.User; // Importuj swoją encję User

import java.util.Optional;

@Repository // Opcjonalne, ale dobra praktyka
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> oznacza:
    // - User: typ encji, którą to repozytorium będzie zarządzać
    // - Long: typ klucza głównego tej encji (nasze User.id jest typu Long)

    // Spring Data JPA automatycznie dostarczy implementacje dla standardowych metod CRUD.

    // Możemy dodać własne metody zapytań, np. do wyszukiwania użytkownika po nazwie użytkownika lub emailu:

    // Znajdź użytkownika po jego nazwie (username)
    Optional<User> findByUsername(String username);

    // Znajdź użytkownika po jego adresie email
    Optional<User> findByEmail(String email);

    // Sprawdź, czy użytkownik o danym username istnieje
    boolean existsByUsername(String username);

    // Sprawdź, czy użytkownik o danym emailu istnieje
    boolean existsByEmail(String email);
}