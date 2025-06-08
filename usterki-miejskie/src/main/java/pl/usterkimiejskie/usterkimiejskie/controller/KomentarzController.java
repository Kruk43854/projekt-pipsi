package pl.usterkimiejskie.usterkimiejskie.controller;

import org.springframework.http.ResponseEntity;
import pl.usterkimiejskie.usterkimiejskie.dto.KomentarzDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Komentarz;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.repository.KomentarzRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;


import java.util.*;

@RestController
@RequestMapping("/api/komentarze")
@CrossOrigin
public class KomentarzController {

    @Autowired
    private KomentarzRepository komentarzRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UsterkaRepository usterkaRepo;

    @GetMapping("/{usterkaId}")
    public List<KomentarzDto> getByUsterka(@PathVariable Long usterkaId) {
        List<Komentarz> komentarze = komentarzRepo.findByUsterkaId(usterkaId);
        return komentarze.stream().map(k -> {
            KomentarzDto dto = new KomentarzDto();
            dto.setId(k.getId());
            dto.setTresc(k.getTresc());
            dto.setAutorUsername(k.getAutor().getUsername());
            dto.setUsterkaId(k.getUsterka().getId());
            dto.setDataDodania(k.getDataDodania()); // jeśli to pole dodasz do DTO
            return dto;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<?> addKomentarz(@RequestBody Map<String, String> payload, HttpSession session) {
        User autor = (User) session.getAttribute("user");
        if (autor == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long usterkaId = Long.parseLong(payload.get("usterkaId"));
        String tresc = payload.get("tresc");

        Usterka usterka = usterkaRepo.findById(usterkaId).orElse(null);
        if (usterka == null) {
            return ResponseEntity.badRequest().body("Usterka not found");
        }

        Komentarz komentarz = new Komentarz();
        komentarz.setTresc(tresc);
        komentarz.setAutor(autor);
        komentarz.setUsterka(usterka);

        Komentarz saved = komentarzRepo.save(komentarz);

        KomentarzDto dto = new KomentarzDto();
        dto.setId(saved.getId());
        dto.setTresc(saved.getTresc());
        dto.setAutorUsername(saved.getAutor().getUsername());
        dto.setUsterkaId(saved.getUsterka().getId());
        dto.setDataDodania(saved.getDataDodania());

        return ResponseEntity.ok(dto);
    }
}
