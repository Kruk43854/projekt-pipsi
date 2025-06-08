package pl.usterkimiejskie.usterkimiejskie.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usterki")
public class Usterka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String miasto;

    @Column(nullable = false, length = 150)
    private String ulica;

    @Column(name = "numer_domu", length = 20)
    private String numerDomu;

    @Lob
    @Column(nullable = false)
    private String opis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusUsterki status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zgloszona_przez_user_id")
    private User zgloszonaPrzez;

    @CreationTimestamp
    @Column(name = "data_zgloszenia", nullable = false, updatable = false)
    private LocalDateTime dataZgloszenia;

    @UpdateTimestamp
    @Column(name = "data_aktualizacji")
    private LocalDateTime dataAktualizacji;

    // Pusty konstruktor wymagany przez JPA
    public Usterka() {
    }

    // Konstruktor z argumentami (przydatny)
    public Usterka(String miasto, String ulica, String numerDomu, String opis, User zgloszonaPrzez) {
        this.miasto = miasto;
        this.ulica = ulica;
        this.numerDomu = numerDomu;
        this.opis = opis;
        this.zgloszonaPrzez = zgloszonaPrzez;
        this.status = StatusUsterki.ZGLOSZONA; // Domyślny status
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

    public User getZgloszonaPrzez() {
        return zgloszonaPrzez;
    }

    public void setZgloszonaPrzez(User zgloszonaPrzez) {
        this.zgloszonaPrzez = zgloszonaPrzez;
    }

    public LocalDateTime getDataZgloszenia() {
        return dataZgloszenia;
    }

    public void setDataZgloszenia(LocalDateTime dataZgloszenia) {
        // Zazwyczaj zarządzane przez @CreationTimestamp, więc setter może nie być używany
        this.dataZgloszenia = dataZgloszenia;
    }

    public LocalDateTime getDataAktualizacji() {
        return dataAktualizacji;
    }

    public void setDataAktualizacji(LocalDateTime dataAktualizacji) {
        // Zazwyczaj zarządzane przez @UpdateTimestamp, więc setter może nie być używany
        this.dataAktualizacji = dataAktualizacji;
    }
}