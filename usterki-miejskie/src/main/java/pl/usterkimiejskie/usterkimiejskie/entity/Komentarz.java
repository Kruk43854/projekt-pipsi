package pl.usterkimiejskie.usterkimiejskie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Komentarz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tresc;
    private LocalDateTime dataDodania;

    @ManyToOne
    private User autor;

    @ManyToOne
    @JoinColumn(name = "usterka_id")
    private Usterka usterka;

    public Komentarz() {
        this.dataDodania = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public LocalDateTime getDataDodania() {
        return dataDodania;
    }

    public void setDataDodania(LocalDateTime dataDodania) {
        this.dataDodania = dataDodania;
    }

    public User getAutor() {
        return autor;
    }

    public void setAutor(User autor) {
        this.autor = autor;
    }

    public Usterka getUsterka() {
        return usterka;
    }

    public void setUsterka(Usterka usterka) {
        this.usterka = usterka;
    }
}
