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
    private String tytul;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 100)
    private String adres;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String opis;

    private double lat;
    private double lng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zgloszona_przez_user_id")
    private User zglaszajacy;

    @CreationTimestamp
    @Column(name = "data_zgloszenia", nullable = false, updatable = false)
    private LocalDateTime dataZgloszenia;

    @UpdateTimestamp
    @Column(name = "data_aktualizacji")
    private LocalDateTime dataAktualizacji;


    public Usterka(){

    }

    public Usterka(String tytul, String status, String adres, String opis,
                   double lat, double lng, User zglaszajacy) {
        this.tytul = tytul;
        this.status = status;
        this.adres = adres;
        this.opis = opis;
        this.lat = lat;
        this.lng = lng;
        this.zglaszajacy = zglaszajacy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdres() { return adres; }
    public void setAdres(String miasto) { this.adres = miasto; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public User getZglaszajacy() { return zglaszajacy; }
    public void setZglaszajacy(User zglaszajacy) { this.zglaszajacy = zglaszajacy; }

    public LocalDateTime getDataZgloszenia() { return dataZgloszenia; }
    public void setDataZgloszenia(LocalDateTime dataZgloszenia) { this.dataZgloszenia = dataZgloszenia; }

    public LocalDateTime getDataAktualizacji() { return dataAktualizacji; }
    public void setDataAktualizacji(LocalDateTime dataAktualizacji) { this.dataAktualizacji = dataAktualizacji; }
}
