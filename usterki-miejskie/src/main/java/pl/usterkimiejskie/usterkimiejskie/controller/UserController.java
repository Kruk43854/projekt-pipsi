package pl.usterkimiejskie.usterkimiejskie.controller;

import pl.usterkimiejskie.usterkimiejskie.entity.User;
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        // Hasło → zakodowane i ustawione w polu passwordHash

        System.out.println("Rejestracja: ");
        System.out.println("-> username: " + user.getUsername());
        System.out.println("-> password: " + user.getPassword());
        System.out.println("-> email: " + user.getEmail());
        System.out.println("-> rola: " + user.getRola());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Domyślnie nowy użytkownik ma rolę "USER"
        if (user.getRola() == null || user.getRola().isBlank()) {
            user.setRola("user");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(userRepo.save(user));
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        Optional<User> user = userRepo.findByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
