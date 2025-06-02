-- Tworzenie tabeli użytkowników
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,           -- Unikalny identyfikator użytkownika (BIGINT, bo może być ich dużo)
                       username VARCHAR(100) NOT NULL UNIQUE,          -- Nazwa użytkownika (login), musi być unikalna
                       email VARCHAR(255) NOT NULL UNIQUE,             -- Adres e-mail, musi być unikalny
                       password_hash VARCHAR(255) NOT NULL,            -- Zahaszowane hasło użytkownika
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,          -- Czy konto użytkownika jest aktywne (domyślnie tak)
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Data utworzenia konta (domyślnie aktualny czas)
    -- Można dodać: updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP (dla MySQL)
    -- lub pole updated_at, które będzie aktualizowane przez aplikację
);

-- Tworzenie tabeli łączącej użytkowników z rolami (relacja wiele-do-wielu)
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,                        -- Klucz obcy wskazujący na ID użytkownika
                            role_id INT NOT NULL,                           -- Klucz obcy wskazujący na ID roli
                            PRIMARY KEY (user_id, role_id),                 -- Klucz główny złożony, aby para (user_id, role_id) była unikalna
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, -- Jeśli użytkownik zostanie usunięty, jego wpisy w tej tabeli też
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE  -- Jeśli rola zostanie usunięta, jej wpisy w tej tabeli też
);

-- Dodajmy przykładowego użytkownika i przypiszmy mu role
-- Pamiętaj, że hasła powinny być hashowane w aplikacji przed zapisaniem do bazy!
-- Tutaj wstawiamy przykładowy, bardzo prosty hash dla celów demonstracyjnych.
-- W prawdziwej aplikacji Spring Security zajmie się hashowaniem.
-- Dla przykładu, zahaszowane 'password123' (np. przez BCrypt)
-- $2a$10$TwojeZahaszowaneHasloDlaUsera...
-- $2a$10$InneZahaszowaneHasloDlaAdmina...

-- Na razie nie będziemy wstawiać użytkowników przez SQL, bo hasła
-- powinny być zarządzane przez aplikację (Spring Security).
-- Zamiast tego, można dodać użytkowników testowych poprzez kod aplikacji później,
-- lub w kolejnej migracji, jeśli mamy już mechanizm hashowania.

-- Możemy jednak stworzyć indeksy dla często używanych kolumn
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);