package pl.usterkimiejskie.usterkimiejskie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.usterkimiejskie.usterkimiejskie.entity.User; // Twoja encja User
import pl.usterkimiejskie.usterkimiejskie.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        // Tworzymy obiekt UserDetails z Spring Security
        // Używamy metod zaimplementowanych w naszej encji User (która implementuje UserDetails)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),      // Z UserDetails (implementowane przez naszą encję User)
                user.getPassword(),      // Z UserDetails (implementowane przez naszą encję User, zwraca passwordHash) <-- TUTAJ POPRAWKA
                user.isEnabled(),        // Z UserDetails (implementowane przez naszą encję User)
                user.isAccountNonExpired(), // Z UserDetails (implementowane przez naszą encję User)
                user.isCredentialsNonExpired(), // Z UserDetails (implementowane przez naszą encję User)
                user.isAccountNonLocked(),  // Z UserDetails (implementowane przez naszą encję User)
                authorities);            // Nasze zmapowane role
    }
}