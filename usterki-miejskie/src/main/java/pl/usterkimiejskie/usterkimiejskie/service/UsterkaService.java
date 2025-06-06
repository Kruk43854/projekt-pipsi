package pl.usterkimiejskie.usterkimiejskie.service;

import org.springframework.stereotype.Service;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class UsterkaService {

    private final UsterkaRepository usterkaRepository;
    private final UserRepository userRepository;

    public UsterkaService(UsterkaRepository usterkaRepository, UserRepository userRepository) {
        this.usterkaRepository = usterkaRepository;
        this.userRepository = userRepository;
    }

    private UsterkaDto mapToDto(Usterka usterka) {
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

    public UsterkaDto createUsterka(UsterkaDto dto) {
       Usterka usterka = new Usterka();
        usterka.setTytul(dto.getTytul());
        usterka.setAdres(dto.getAdres());
        usterka.setLat(dto.getLat());
        usterka.setLng(dto.getLng());
        usterka.setStatus("Oczekuje");
        usterka.setDataZgloszenia(LocalDateTime.now());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
            usterka.setZglaszajacy(user);
        }

        Usterka saved = usterkaRepository.save(usterka);
        return mapToDto(saved);
    }
    public UsterkaDto findById(Long id) {
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono usterki"));
        return mapToDto(usterka);
    }
    public void zmienStatus(Long id, String status) {
        Usterka usterka = usterkaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono usterki"));
        usterka.setStatus(status);
        usterkaRepository.save(usterka);
    }
}
