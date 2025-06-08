package pl.usterkimiejskie.usterkimiejskie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Usuń import lombok.Data jeśli dodajesz metody ręcznie
import pl.usterkimiejskie.usterkimiejskie.entity.StatusUsterki;

import java.time.LocalDateTime;

// @Data // USUWAMY LUB ZASTĘPUJEMY @Getter, @Setter, @NoArgsConstructor ITD.
public class UsterkaDto {

    private Long id;

    @NotBlank(message = "Miasto nie może być puste")
    @Size(min = 2, max = 100, message = "Nazwa miasta musi mieć od 2 do 100 znaków")
    private String miasto;

    @NotBlank(message = "Ulica nie może być pusta")
    @Size(min = 2, max = 100, message = "Nazwa ulicy musi mieć od 2 do 100 znaków")
    private String ulica;

    private String numerDomu;

    @NotBlank(message = "Opis usterki nie może być pusty")
    @Size(min = 10, max = 1000, message = "Opis musi mieć od 10 do 1000 znaków")
    private String opis;

    private StatusUsterki status;

    private String zgloszonaPrzezUsername;

    private LocalDateTime dataZgloszenia;
    private LocalDateTime dataAktualizacji;

    // Pusty konstruktor (ważny dla niektórych frameworków, np. Jackson do deserializacji JSON)
    public UsterkaDto() {
    }

    // Gettery i Settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public String getNumerDomu() {
        return numerDomu;
    }

    public void setNumerDomu(String numerDomu) {
        this.numerDomu = numerDomu;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public StatusUsterki getStatus() {
        return status;
    }

    public void setStatus(StatusUsterki status) {
        this.status = status;
    }

    public String getZgloszonaPrzezUsername() {
        return zgloszonaPrzezUsername;
    }

    public void setZgloszonaPrzezUsername(String zgloszonaPrzezUsername) {
        this.zgloszonaPrzezUsername = zgloszonaPrzezUsername;
    }

    public LocalDateTime getDataZgloszenia() {
        return dataZgloszenia;
    }

    public void setDataZgloszenia(LocalDateTime dataZgloszenia) {
        this.dataZgloszenia = dataZgloszenia;
    }

    public LocalDateTime getDataAktualizacji() {
        return dataAktualizacji;
    }

    public void setDataAktualizacji(LocalDateTime dataAktualizacji) {
        this.dataAktualizacji = dataAktualizacji;
    }
}