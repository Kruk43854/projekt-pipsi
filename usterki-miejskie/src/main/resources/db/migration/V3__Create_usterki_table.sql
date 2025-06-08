CREATE TABLE usterki (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,               -- Unikalny identyfikator usterki
                         tytul VARCHAR(255) NOT NULL,

--                          miasto VARCHAR(100) NOT NULL,                       -- Miasto, w którym wystąpiła usterka
--                          ulica VARCHAR(150) NOT NULL,                        -- Ulica
--                          numer_domu VARCHAR(20),                             -- Numer domu/budynku (opcjonalnie)
                         adres VARCHAR(150) NOT NULL,


                         lat DECIMAL(9,6),
                         lng DECIMAL(9,6),

--                          opis TEXT NOT NULL,                                 -- Szczegółowy opis usterki
                         status VARCHAR(50) NOT NULL DEFAULT 'Oczekuje',    -- Aktualny status usterki
    -- Domyślnie 'ZGLOSZONA', inne możliwe wartości:
    -- POTWIERDZONA, W_TRAKCIE_NAPRAWY, NAPRAWIONA, ODRZUCONA

    -- Opcjonalne pola, które mogą się przydać w przyszłości:
    -- szerokosc_geo DECIMAL(9,6),                      -- Szerokość geograficzna
    -- dlugosc_geo DECIMAL(10,6),                       -- Długość geograficzna
    -- kategoria VARCHAR(100),                          -- Kategoria usterki (np. Drogi, Oświetlenie, Zieleń)
    -- sciezka_do_zdjecia VARCHAR(255),                 -- Ścieżka do ewentualnego załączonego zdjęcia

                         zgloszona_przez_user_id BIGINT,                     -- ID użytkownika, który zgłosił usterkę
    -- Może być NULL, jeśli dopuszczamy anonimowe zgłoszenia (do przemyślenia)
                         data_zgloszenia TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Data i czas zgłoszenia
                         data_aktualizacji TIMESTAMP NULL DEFAULT NULL,      -- Data i czas ostatniej aktualizacji statusu/danych usterki
    -- Można użyć ON UPDATE CURRENT_TIMESTAMP w MySQL,
    -- lub aktualizować programowo w aplikacji

                         FOREIGN KEY (zgloszona_przez_user_id) REFERENCES users(id) ON DELETE SET NULL
    -- Jeśli użytkownik zostanie usunięty, jego zgłoszenia pozostaną, ale bez przypisanego zgłaszającego.
    -- Alternatywnie można użyć ON DELETE RESTRICT, aby uniemożliwić usunięcie użytkownika, który ma zgłoszenia,
    -- lub ON DELETE CASCADE, aby usunąć zgłoszenia wraz z użytkownikiem (ostrożnie!).
);

-- Indeksy dla często wyszukiwanych kolumn
-- CREATE INDEX idx_usterki_miasto ON usterki(miasto);
CREATE INDEX idx_usterki_status ON usterki(status);
CREATE INDEX idx_usterki_zgloszona_przez ON usterki(zgloszona_przez_user_id);