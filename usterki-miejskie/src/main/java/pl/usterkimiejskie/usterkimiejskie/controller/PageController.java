package pl.usterkimiejskie.usterkimiejskie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/strona-glowna")
    public String stronaGlowna() {return "index.html";}

    @GetMapping("/")
    public String home() {
        return "index.html";
    }

    @GetMapping("/logowanie")
    public String login() {
        return "/login.html";
    }

    @GetMapping("/rejestracja")
    public String register() {
        return "/register.html";
    }

    @GetMapping("/lista-usterek")
    public String listaUsterek() {
        return "/lista_usterek.html";
    }

    @GetMapping("/moje-zgloszenia")
    public String mojeZgloszenia() {
        return "/moje_zgloszenia.html";
    }

    @GetMapping("/dodaj-usterke")
    public String dodajUsterke() {
        return "/dodaj_usterke.html";
    }

    @GetMapping("/usterka")
    public String usterka() {
        return "/usterka.html";
    }

    @GetMapping("/admin-lista")
    public String adminLista() {
        return "/admin_lista.html";
    }

    @GetMapping("/admin_usterka")
    public String adminUsterka() {
        return "/admin_usterka.html";
    }

    @GetMapping("/ustawienia")
    public String ustawienia() {
        return "/ustawienia.html";
    }

    @GetMapping("/o-aplikacji")
    public String about() {
        return "/about.html";
    }
}
