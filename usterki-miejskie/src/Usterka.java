// Klasa: Usterka
@Entity
public class Usterka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tytul;
    private String status;
    private String adres;
    private double lat;
    private double lng;

    @ManyToOne
    @JoinColumn(name = "user_id")                    // nazwa kolumny w tabeli usterka
    @JsonIgnoreProperties("usterki")                  // unika zapętlenia JSON (jeśli w User masz List<Usterka>)
    private User user;

    public Usterka() {
        // Wymagany pusty konstruktor dla JPA
    }

    public Usterka(String tytul, String status, String adres, double lat, double lng, User user) {
        this.tytul = tytul;
        this.status = status;
        this.adres = adres;
        this.lat = lat;
        this.lng = lng;
        this.user = user;
    }
}
