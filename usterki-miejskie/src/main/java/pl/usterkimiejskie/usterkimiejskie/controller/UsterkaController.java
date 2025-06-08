package pl.usterkimiejskie.usterkimiejskie.controller; // Upewnij się, że nazwa pakietu jest poprawna

import jakarta.validation.Valid; // Dla walidacji DTO przychodzących w żądaniu
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.service.UsterkaService;

import java.util.List;

@RestController // Kluczowa adnotacja: Mówi Springowi, że ta klasa jest kontrolerem REST
// i że metody domyślnie zwracają dane, które zostaną przekonwertowane na JSON.
@RequestMapping("/api/usterki") // Definiuje bazowy URL dla wszystkich endpointów w tym kontrolerze.
// Wszystkie żądania do /api/usterki/... będą tu kierowane.
public class UsterkaController {

    private final UsterkaService usterkaService;

    @Autowired // Wstrzykiwanie zależności UsterkaService
    public UsterkaController(UsterkaService usterkaService) {
        this.usterkaService = usterkaService;
    }

    // Endpoint do pobierania wszystkich usterek
    // HTTP GET na /api/usterki
    @GetMapping
    public ResponseEntity<List<UsterkaDto>> getAllUsterki() {
        List<UsterkaDto> usterki = usterkaService.getAllUsterki();
        return ResponseEntity.ok(usterki); // Zwraca listę usterek z kodem HTTP 200 OK
    }

    // Endpoint do pobierania usterki po jej ID
    // HTTP GET na /api/usterki/{id} (gdzie {id} to numer ID usterki)
    @GetMapping("/{id}")
    public ResponseEntity<UsterkaDto> getUsterkaById(@PathVariable Long id) {
        // @PathVariable Long id - pobiera wartość 'id' ze ścieżki URL
        UsterkaDto usterkaDto = usterkaService.getUsterkaById(id);
        return ResponseEntity.ok(usterkaDto);
        // W przyszłości dodamy obsługę sytuacji, gdy usterka nie zostanie znaleziona (np. zwrot 404 Not Found)
    }

    // Endpoint do tworzenia nowej usterki
    // HTTP POST na /api/usterki
    @PostMapping
    public ResponseEntity<UsterkaDto> createUsterka(@Valid @RequestBody UsterkaDto usterkaDto) {
        // @RequestBody UsterkaDto usterkaDto - dane usterki przychodzą w ciele żądania HTTP (w formacie JSON)
        //                                      i są automatycznie mapowane na obiekt UsterkaDto.
        // @Valid - włącza walidację dla obiektu usterkaDto (na podstawie adnotacji @NotBlank, @Size itp. w UsterkaDto)

        // Na razie username zgłaszającego będzie zakodowany na stałe.
        // Później będziemy go pobierać od zalogowanego użytkownika (Spring Security).
        String przykladowyUsernameZglaszajacego = "testuser"; // TODO: Zastąpić dynamicznym użytkownikiem

        // TODO: Przed wywołaniem createUsterka, upewnij się, że użytkownik "testuser" istnieje w bazie.
        //       Możesz go dodać przez SQL w migracji V2 lub tymczasowo stworzyć go przy starcie aplikacji
        //       w komponencie CommandLineRunner. Na razie zakładamy, że istnieje.

        UsterkaDto createdUsterka = usterkaService.createUsterka(usterkaDto, przykladowyUsernameZglaszajacego);
        return new ResponseEntity<>(createdUsterka, HttpStatus.CREATED); // Zwraca stworzoną usterkę z kodem HTTP 201 Created
    }

    // Endpoint do aktualizacji istniejącej usterki
    // HTTP PUT na /api/usterki/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsterkaDto> updateUsterka(@PathVariable Long id, @Valid @RequestBody UsterkaDto usterkaDto) {
        UsterkaDto updatedUsterka = usterkaService.updateUsterka(id, usterkaDto);
        return ResponseEntity.ok(updatedUsterka);
    }

    // Endpoint do usuwania usterki
    // HTTP DELETE na /api/usterki/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsterka(@PathVariable Long id) {
        usterkaService.deleteUsterka(id);
        return ResponseEntity.noContent().build(); // Zwraca kod HTTP 204 No Content (sukces, brak treści do zwrotu)
    }
}