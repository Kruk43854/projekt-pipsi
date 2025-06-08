package pl.usterkimiejskie.usterkimiejskie.service;

import org.springframework.stereotype.Service;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;

@Service
public class UsterkaService {

    private static final Logger logger = LoggerFactory.getLogger(UsterkaService.class);

    private final UsterkaRepository usterkaRepository;
    private final UserRepository userRepository;

    public UsterkaService(UsterkaRepository usterkaRepository, UserRepository userRepository) {
        this.usterkaRepository = usterkaRepository;
        this.userRepository = userRepository;
    }

    public UsterkaDto mapToDto(Usterka usterka) {
        UsterkaDto dto = new UsterkaDto();
        dto.setId(usterka.getId());
        dto.setTytul(usterka.getTytul());
        dto.setAdres(usterka.getAdres());
        dto.setLat(usterka.getLat());
        dto.setLng(usterka.getLng());
        dto.setStatus(usterka.getStatus());
        dto.setUserId(usterka.getZglaszajacy() != null ? usterka.getZglaszajacy().getId() : null);
        return dto;
    }

    public List<UsterkaDto> findAll() {
        return usterkaRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Usterka createUsterka(UsterkaDto dto, User user) {
        Usterka usterka = new Usterka();
        usterka.setTytul(dto.getTytul());
        usterka.setAdres(dto.getAdres());
        usterka.setLat(dto.getLat());
        usterka.setLng(dto.getLng());
        usterka.setStatus("Oczekuje");
        usterka.setDataZgloszenia(LocalDateTime.now());
        usterka.setZglaszajacy(user);

        logger.debug("Tworzenie nowej usterki: tytul='{}', adres='{}', userId={}",
                dto.getTytul(), dto.getAdres(), dto.getUserId());

        return usterkaRepository.save(usterka);
    }

    
    public UsterkaDto findById(Long id) {
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono usterki"));
        return mapToDto(usterka);
    }
    public Usterka zmienStatus(Long id, String status) {
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono usterki"));
        usterka.setStatus(status);
        logger.info("Zmieniono status usterki ID={}  na '{}'", id, status);
        return usterkaRepository.save(usterka);
    }
}
