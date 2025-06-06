CREATE TABLE komentarz
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    tresc        VARCHAR(255) NULL,
    data_dodania datetime NULL,
    autor_id     BIGINT NULL,
    usterka_id   BIGINT NULL,
    CONSTRAINT pk_komentarz PRIMARY KEY (id)
);

ALTER TABLE komentarz
    ADD CONSTRAINT FK_KOMENTARZ_ON_AUTOR FOREIGN KEY (autor_id) REFERENCES users (id);

ALTER TABLE komentarz
    ADD CONSTRAINT FK_KOMENTARZ_ON_USTERKA FOREIGN KEY (usterka_id) REFERENCES usterki (id);