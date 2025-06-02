package pl.usterkimiejskie.usterkimiejskie.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    // Pusty konstruktor wymagany przez JPA
    public Role() {
    }

    // Konstruktor z argumentami (przydatny)
    public Role(String name) {
        this.name = name;
    }

    // Gettery i Settery
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}