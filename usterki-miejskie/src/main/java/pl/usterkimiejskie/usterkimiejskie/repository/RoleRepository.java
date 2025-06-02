package pl.usterkimiejskie.usterkimiejskie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.usterkimiejskie.usterkimiejskie.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Spring Data JPA automatycznie dostarczy implementacje dla standardowych metod:
    // save(), findById(), findAll(), deleteById(), count(), existsById(), itp.

    // Możemy też definiować własne metody zapytań, np. do wyszukiwania roli po nazwie:
    Optional<Role> findByName(String name);
    // Spring Data JPA automatycznie zrozumie, że ma wygenerować zapytanie
    // SELECT * FROM roles WHERE name = ? na podstawie nazwy tej metody.
    // Optional<Role> oznacza, że metoda może zwrócić rolę lub nic (jeśli rola o danej nazwie nie istnieje).
}