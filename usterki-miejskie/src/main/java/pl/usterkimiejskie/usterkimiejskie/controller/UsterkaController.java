package pl.usterkimiejskie.usterkimiejskie.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.usterkimiejskie.usterkimiejskie.dto.UsterkaDto;
import pl.usterkimiejskie.usterkimiejskie.service.UsterkaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usterki")
public class UsterkaController {

    private final UsterkaService usterkaService;

    public UsterkaController(UsterkaService usterkaService) {
        this.usterkaService = usterkaService;
    }

    @GetMapping
    public List<UsterkaDto> getAllUsterki() {
        return usterkaService.findAll();
    }

    @PostMapping
    public ResponseEntity<UsterkaDto> createUsterka(@RequestBody UsterkaDto dto) {
        UsterkaDto result = usterkaService.createUsterka(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> zmienStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        usterkaService.zmienStatus(id, status);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsterkaDto> getUsterkaById(@PathVariable Long id) {
        UsterkaDto dto = usterkaService.findById(id);
        return ResponseEntity.ok(dto);
    }
}
