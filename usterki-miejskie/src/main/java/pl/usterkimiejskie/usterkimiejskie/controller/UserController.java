package pl.usterkimiejskie.usterkimiejskie.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import pl.usterkimiejskie.usterkimiejskie.service.EmailService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAll() {
        return userRepo.findAll();
    }
    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        // Hasło → zakodowane i ustawione w polu passwordHash
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Domyślnie nowy użytkownik ma rolę "USER"
        if (user.getRola() == null || user.getRola().isBlank()) {
            user.setRola("user");
        }

        User savedUser = userRepo.save(user);

        logger.info("Zarejestrowano nowego użytkownika: username='{}', email='{}'",
                savedUser.getUsername(), savedUser.getEmail());

        emailService.wyslijMail(
                savedUser.getEmail(),
                "Rejestracja zakończona",
                "Witaj " + savedUser.getUsername() +"! \n Twoje konto zostało pomyślnie utworzone. Możesz zalogować się do serwisu."
        );


        return ResponseEntity.status(HttpStatus.CREATED).body(userRepo.save(user));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> existingUserOpt = userRepo.findById(id);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOpt.get();
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepo.save(existingUser);
        emailService.wyslijMail(
                existingUser.getEmail(),
                "Zaktualizowano dane konta",
                "Witaj, " + existingUser.getUsername() + ",\n\n" +
                        "Informujemy, że Twoje dane konta zostały właśnie zmodyfikowane.\n\n" +
                        "Jeśli to nie Ty dokonałeś tej zmiany, jak najszybciej skontaktuj się z administracją."
        );
        return ResponseEntity.ok().build();
    }


    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
