package pl.usterkimiejskie.usterkimiejskie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.entity.Usterka;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.service.EmailService;
import pl.usterkimiejskie.usterkimiejskie.service.UsterkaService;
import jakarta.servlet.http.HttpSession;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usterki")
public class UsterkaController {

    private final UsterkaService usterkaService;

    @Autowired
    private EmailService emailService;

    public UsterkaController(UsterkaService usterkaService) {
        this.usterkaService = usterkaService;
    }

    @GetMapping
    public List<UsterkaDto> getAllUsterki() {
        return usterkaService.findAll();
    }

    @PostMapping
    public ResponseEntity<UsterkaDto> createUsterka(@RequestBody UsterkaDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usterka usterka = usterkaService.createUsterka(dto, user);

        emailService.wyslijMail(
                user.getEmail(),
                "Nowe zgłoszenie usterki",
                "Dziękujemy za zgłoszenie usterki: " +
                        "Tytuł: " + usterka.getTytul() + "\n" +
                        "Adres: " + usterka.getAdres() + "\n\n" +
                        "Będziemy informować o dalszej naprawie zgłoszonej przez Ciebie usterki."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(usterkaService.mapToDto(usterka));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> zmienStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Usterka usterka = usterkaService.zmienStatus(id, status); // teraz mamy obiekt usterki

        if (usterka.getZglaszajacy() != null && usterka.getZglaszajacy().getEmail() != null) {
            emailService.wyslijMail(
                    usterka.getZglaszajacy().getEmail(),
                    "Zmiana statusu usterki",
                    "Status zgłoszonej usterki:\n" +
                            "Tytuł: " + usterka.getTytul() + "\n" +
                            "Adres: "+ usterka.getAdres() + "\n" +
                            "Nowy status: " + usterka.getStatus()
            );
        }

        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UsterkaDto> getUsterkaById(@PathVariable Long id) {
        UsterkaDto dto = usterkaService.findById(id);
        return ResponseEntity.ok(dto);
    }
}
