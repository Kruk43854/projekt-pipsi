package pl.usterkimiejskie.usterkimiejskie.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority; // Dodaj import
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Dodaj import
import org.springframework.security.core.userdetails.UserDetails; // Dodaj import

import java.time.LocalDateTime;
import java.util.Collection; // Dodaj import
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors; // Dodaj import

@Entity
@Table(name = "users")
public class User implements UserDetails { // Implementujemy UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.enabled = true;
    }

    // Gettery i Settery (pozostają bez zmian)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // username jest już metodą z UserDetails, więc getter jest "nadpisywany"
    // public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    // passwordHash - getter nazywa się getPassword() w UserDetails
    // public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    // isEnabled jest już metodą z UserDetails
    // public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void addRole(Role role) { this.roles.add(role); }
    public void removeRole(Role role) { this.roles.remove(role); }

    // Implementacja metod z interfejsu UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapujemy nasze Role na GrantedAuthority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        // UserDetails oczekuje metody getPassword(), a my przechowujemy hash w passwordHash
        return passwordHash;
    }

    @Override
    public String getUsername() {
        // UserDetails oczekuje metody getUsername()
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Możesz dodać logikę, jeśli konta mogą wygasać
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Możesz dodać logikę, jeśli konta mogą być blokowane
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Możesz dodać logikę, jeśli hasła mogą wygasać
    }

    @Override
    public boolean isEnabled() {
        // Używamy naszego pola enabled
        return enabled;
    }
}