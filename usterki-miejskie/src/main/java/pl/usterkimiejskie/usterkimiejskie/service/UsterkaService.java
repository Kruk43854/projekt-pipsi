package pl.usterkimiejskie.usterkimiejskie.service;

import jakarta.persistence.EntityNotFoundException; // Standardowy wyjątek JPA
import org.springframework.beans.factory.annotation.Autowired; // Można używać, ale preferowane wstrzykiwanie przez konstruktor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Dla zarządzania transakcjami
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.entity.StatusUsterki;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository; // Będziemy potrzebować do przypisywania użytkownika

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsterkaService {

    private final UsterkaRepository usterkaRepository;
    private final UserRepository userRepository; // Dodajemy repozytorium użytkowników

    // Wstrzykiwanie zależności przez konstruktor
    @Autowired // Ta adnotacja jest opcjonalna na konstruktorze od pewnej wersji Springa, jeśli jest tylko jeden konstruktor
    public UsterkaService(UsterkaRepository usterkaRepository, UserRepository userRepository) {
        this.usterkaRepository = usterkaRepository;
        this.userRepository = userRepository;
    }

    // Metoda do mapowania Encji Usterka na UsterkaDto
    private UsterkaDto mapToDto(Usterka usterka) {
        UsterkaDto dto = new UsterkaDto();
        dto.setId(usterka.getId());
        dto.setMiasto(usterka.getMiasto());
        dto.setUlica(usterka.getUlica());
        dto.setNumerDomu(usterka.getNumerDomu());
        dto.setOpis(usterka.getOpis());
        dto.setStatus(usterka.getStatus());
        if (usterka.getZgloszonaPrzez() != null) {
            dto.setZgloszonaPrzezUsername(usterka.getZgloszonaPrzez().getUsername());
        }
        dto.setDataZgloszenia(usterka.getDataZgloszenia());
        dto.setDataAktualizacji(usterka.getDataAktualizacji());
        return dto;
    }

    // Metoda do mapowania UsterkaDto na Encję Usterka (używana przy tworzeniu/aktualizacji)
    // Tutaj nie mapujemy ID, bo jest generowane przez bazę lub pochodzi z URL przy aktualizacji
    // Nie mapujemy też zgloszonaPrzezUsername bezpośrednio, bo to będzie obsługiwane osobno
    private Usterka mapToEntity(UsterkaDto usterkaDto) {
        Usterka usterka = new Usterka();
        // ID jest ustawiane przez bazę (przy tworzeniu) lub pobierane z repozytorium (przy aktualizacji)
        usterka.setMiasto(usterkaDto.getMiasto());
        usterka.setUlica(usterkaDto.getUlica());
        usterka.setNumerDomu(usterkaDto.getNumerDomu());
        usterka.setOpis(usterkaDto.getOpis());
        // Status i zgloszonaPrzez będą ustawiane w logice serwisu
        // Jeśli status przychodzi w DTO, można go tu ustawić, ale często domyślny jest ZGLOSZONA
        if (usterkaDto.getStatus() != null) {
            usterka.setStatus(usterkaDto.getStatus());
        } else {
            usterka.setStatus(StatusUsterki.ZGLOSZONA); // Domyślny status
        }
        return usterka;
    }

    @Transactional(readOnly = true) // Transakcja tylko do odczytu - bardziej wydajna
    public List<UsterkaDto> getAllUsterki() {
        return usterkaRepository.findAll() // Pobierz wszystkie encje Usterka
                .stream()                  // Przekształć listę na strumień
                .map(this::mapToDto)       // Zmapuj każdą encję Usterka na UsterkaDto
                .collect(Collectors.toList()); // Zbierz wyniki do nowej listy DTO
    }

    @Transactional(readOnly = true)
    public UsterkaDto getUsterkaById(Long id) {
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono usterki o ID: " + id));
        return mapToDto(usterka);
    }

    @Transactional // Pełna transakcja (odczyt i zapis)
    public UsterkaDto createUsterka(UsterkaDto usterkaDto, String usernameZglaszajacego) {
        Usterka usterka = mapToEntity(usterkaDto);

        // Znajdź użytkownika, który zgłasza usterkę
        // W prawdziwej aplikacji, usernameZglaszajacego pochodziłoby z danych uwierzytelniających (np. Spring Security)
        User zglaszajacy = userRepository.findByUsername(usernameZglaszajacego)
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik o nazwie '" + usernameZglaszajacego + "' nie został znaleziony."));

        usterka.setZgloszonaPrzez(zglaszajacy);
        // Data zgłoszenia i aktualizacji ustawi się automatycznie dzięki @CreationTimestamp i @UpdateTimestamp

        Usterka savedUsterka = usterkaRepository.save(usterka); // Zapisz nową usterkę do bazy
        return mapToDto(savedUsterka); // Zwróć DTO zapisanej usterki
    }

    @Transactional
    public UsterkaDto updateUsterka(Long id, UsterkaDto usterkaDto) {
        Usterka existingUsterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono usterki o ID: " + id + " do aktualizacji."));

        // Aktualizuj tylko te pola, które mogą być zmienione
        existingUsterka.setMiasto(usterkaDto.getMiasto());
        existingUsterka.setUlica(usterkaDto.getUlica());
        existingUsterka.setNumerDomu(usterkaDto.getNumerDomu());
        existingUsterka.setOpis(usterkaDto.getOpis());

        // Status może być aktualizowany przez inną, bardziej specyficzną metodę/logikę
        if (usterkaDto.getStatus() != null) {
            existingUsterka.setStatus(usterkaDto.getStatus());
        }
        // dataAktualizacji ustawi się automatycznie dzięki @UpdateTimestamp

        Usterka updatedUsterka = usterkaRepository.save(existingUsterka);
        return mapToDto(updatedUsterka);
    }

    @Transactional
    public void deleteUsterka(Long id) {
        if (!usterkaRepository.existsById(id)) {
            throw new EntityNotFoundException("Nie znaleziono usterki o ID: " + id + " do usunięcia.");
        }
        usterkaRepository.deleteById(id);
    }
}