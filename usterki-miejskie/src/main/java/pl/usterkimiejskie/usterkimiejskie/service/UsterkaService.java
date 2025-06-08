package pl.usterkimiejskie.usterkimiejskie.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.entity.StatusUsterki;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsterkaService {

    private static final Logger logger = LoggerFactory.getLogger(UsterkaService.class);

    private final UsterkaRepository usterkaRepository;
    private final UserRepository userRepository;
    private final EmailService emailService; // Dodajemy EmailService

    @Autowired
    public UsterkaService(UsterkaRepository usterkaRepository,
                          UserRepository userRepository,
                          EmailService emailService) { // Dodajemy EmailService do konstruktora
        this.usterkaRepository = usterkaRepository;
        this.userRepository = userRepository;
        this.emailService = emailService; // Przypisujemy wstrzyknięty EmailService
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

    // Metoda do mapowania UsterkaDto na Encję Usterka
    private Usterka mapToEntity(UsterkaDto usterkaDto) {
        Usterka usterka = new Usterka();
        usterka.setMiasto(usterkaDto.getMiasto());
        usterka.setUlica(usterkaDto.getUlica());
        usterka.setNumerDomu(usterkaDto.getNumerDomu());
        usterka.setOpis(usterkaDto.getOpis());
        if (usterkaDto.getStatus() != null) {
            usterka.setStatus(usterkaDto.getStatus());
        } else {
            usterka.setStatus(StatusUsterki.ZGLOSZONA);
        }
        return usterka;
    }

    @Transactional(readOnly = true)
    public List<UsterkaDto> getAllUsterki() {
        logger.info("Pobieranie wszystkich usterek");
        List<UsterkaDto> usterki = usterkaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        logger.debug("Pobrano {} usterek", usterki.size());
        return usterki;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usterkaCache", key = "#id")
    public UsterkaDto getUsterkaById(Long id) {
        logger.info("Pobieranie usterki o ID: {} (metoda serwisowa wykonana - brak w cache lub pierwsze wywołanie)", id);
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Nie znaleziono usterki o ID: {}", id);
                    return new EntityNotFoundException("Nie znaleziono usterki o ID: " + id);
                });
        logger.debug("Znaleziono usterkę: {}", usterka.getOpis());
        return mapToDto(usterka);
    }

    @Transactional
    @CacheEvict(value = "usterkaCache", allEntries = true)
    public UsterkaDto createUsterka(UsterkaDto usterkaDto, String usernameZglaszajacego) {
        logger.info("Tworzenie nowej usterki przez użytkownika: {} z danymi: {}", usernameZglaszajacego, usterkaDto);

        Usterka usterka = mapToEntity(usterkaDto);

        User zglaszajacy = userRepository.findByUsername(usernameZglaszajacego)
                .orElseThrow(() -> {
                    logger.error("Nie można utworzyć usterki. Użytkownik o nazwie '{}' nie został znaleziony.", usernameZglaszajacego);
                    return new EntityNotFoundException("Użytkownik o nazwie '" + usernameZglaszajacego + "' nie został znaleziony.");
                });

        usterka.setZgloszonaPrzez(zglaszajacy);

        try {
            Usterka savedUsterka = usterkaRepository.save(usterka);
            logger.info("Pomyślnie utworzono usterkę o ID: {}", savedUsterka.getId());

            // Wysyłanie e-maila po utworzeniu usterki
            if (zglaszajacy.getEmail() != null && !zglaszajacy.getEmail().isEmpty()) {
                String temat = "Potwierdzenie zgłoszenia usterki #" + savedUsterka.getId();
                String tresc = "Dzień dobry " + zglaszajacy.getUsername() + ",\n\n" +
                        "Dziękujemy za zgłoszenie usterki w portalu Usterek Miejskich.\n\n" +
                        "Szczegóły zgłoszenia:\n" +
                        "ID Usterki: " + savedUsterka.getId() + "\n" +
                        "Miasto: " + savedUsterka.getMiasto() + "\n" +
                        "Ulica: " + savedUsterka.getUlica() + (savedUsterka.getNumerDomu() != null ? " " + savedUsterka.getNumerDomu() : "") + "\n" +
                        "Opis: " + savedUsterka.getOpis() + "\n" +
                        "Status: " + savedUsterka.getStatus().toString() + "\n\n" +
                        "Pozdrawiamy,\nZespół Usterek Miejskich";
                emailService.sendSimpleMessage(zglaszajacy.getEmail(), temat, tresc);
            } else {
                logger.warn("Nie można wysłać e-maila do użytkownika '{}' - brak adresu e-mail.", zglaszajacy.getUsername());
            }

            return mapToDto(savedUsterka);
        } catch (Exception e) {
            logger.error("Błąd podczas zapisu usterki do bazy danych dla użytkownika {}: {}", usernameZglaszajacego, usterkaDto, e);
            throw e;
        }
    }

    @Transactional
    @CachePut(value = "usterkaCache", key = "#id")
    public UsterkaDto updateUsterka(Long id, UsterkaDto usterkaDto) {
        logger.info("Aktualizacja usterki o ID: {} z danymi: {}", id, usterkaDto);
        Usterka existingUsterka = usterkaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Nie znaleziono usterki o ID: {} do aktualizacji.", id);
                    return new EntityNotFoundException("Nie znaleziono usterki o ID: " + id + " do aktualizacji.");
                });

        existingUsterka.setMiasto(usterkaDto.getMiasto());
        existingUsterka.setUlica(usterkaDto.getUlica());
        existingUsterka.setNumerDomu(usterkaDto.getNumerDomu());
        existingUsterka.setOpis(usterkaDto.getOpis());
        if (usterkaDto.getStatus() != null) {
            // TODO: Rozważyć logikę, kto może zmieniać status i na jaki, np. wysyłanie maila o zmianie statusu
            logger.info("Zmiana statusu usterki ID {} z {} na {}", id, existingUsterka.getStatus(), usterkaDto.getStatus());
            existingUsterka.setStatus(usterkaDto.getStatus());
        }

        try {
            Usterka updatedUsterka = usterkaRepository.save(existingUsterka);
            logger.info("Pomyślnie zaktualizowano usterkę o ID: {}", updatedUsterka.getId());
            return mapToDto(updatedUsterka);
        } catch (Exception e) {
            logger.error("Błąd podczas aktualizacji usterki o ID {}: {}", id, usterkaDto, e);
            throw e;
        }
    }

    @Transactional
    @CacheEvict(value = "usterkaCache", key = "#id")
    public void deleteUsterka(Long id) {
        logger.info("Usuwanie usterki o ID: {}", id);
        if (!usterkaRepository.existsById(id)) {
            logger.warn("Próba usunięcia nieistniejącej usterki o ID: {}", id);
            throw new EntityNotFoundException("Nie znaleziono usterki o ID: " + id + " do usunięcia.");
        }
        try {
            usterkaRepository.deleteById(id);
            logger.info("Pomyślnie usunięto usterkę o ID: {}", id);
        } catch (Exception e) {
            logger.error("Błąd podczas usuwania usterki o ID {}:", id, e);
            throw e;
        }
    }
}