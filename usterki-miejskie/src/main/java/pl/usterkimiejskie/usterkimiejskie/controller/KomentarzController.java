package pl.usterkimiejskie.usterkimiejskie.controller;

import pl.usterkimiejskie.usterkimiejskie.entity.Komentarz;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.repository.KomentarzRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import pl.usterkimiejskie.usterkimiejskie.repository.UsterkaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public List<Komentarz> getByUsterka(@PathVariable Long usterkaId) {
        return komentarzRepo.findByUsterkaId(usterkaId);
    }

    @PostMapping
    public Komentarz addKomentarz(@RequestBody Map<String, String> payload) {
        Long userId = Long.parseLong(payload.get("userId"));
        Long usterkaId = Long.parseLong(payload.get("usterkaId"));
        String tresc = payload.get("tresc");

        User autor = userRepo.findById(userId).orElse(null);
        Usterka usterka = usterkaRepo.findById(usterkaId).orElse(null);

        Komentarz komentarz = new Komentarz();
        komentarz.setTresc(tresc);
        komentarz.setAutor(autor);
        komentarz.setUsterka(usterka);

        return komentarzRepo.save(komentarz);
    }
}
