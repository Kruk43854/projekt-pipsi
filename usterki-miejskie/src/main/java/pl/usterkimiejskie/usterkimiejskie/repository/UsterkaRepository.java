package pl.usterkimiejskie.usterkimiejskie.repository; // Upewnij się, że nazwa pakietu jest poprawna

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.usterkimiejskie.usterkimiejskie.entity.StatusUsterki; // Importuj enum StatusUsterki
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;   // Importuj encję Usterka
import pl.usterkimiejskie.usterkimiejskie.entity.User;     // Importuj encję User

import java.util.List;

@Repository
public interface UsterkaRepository extends JpaRepository<Usterka, Long> {
    // JpaRepository<Usterka, Long> oznacza:
    // - Usterka: typ encji
    // - Long: typ klucza głównego encji Usterka (Usterka.id jest typu Long)

    // Przykładowe metody zapytań, które mogą się przydać:


    // Znajdź wszystkie usterki o danym statusie
    List<Usterka> findByStatus(StatusUsterki status);

    // Znajdź wszystkie usterki zgłoszone przez danego użytkownika
//    List<Usterka> findByUser(User zgloszonaPrzez);

    // Znajdź wszystkie usterki w danym mieście i o danym statusie
//    List<Usterka> findByMiastoAndStatus(String miasto, StatusUsterki status);
}