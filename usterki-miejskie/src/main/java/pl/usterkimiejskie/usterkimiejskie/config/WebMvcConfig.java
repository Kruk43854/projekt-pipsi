package pl.usterkimiejskie.usterkimiejskie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver; // Użyjemy tego dla REST API

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Definiujemy listę wspieranych języków
    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            new Locale("pl"), // Polski
            new Locale("en")  // Angielski
    );

    @Bean
    public LocaleResolver localeResolver() {
        // Dla REST API, AcceptHeaderLocaleResolver jest często najlepszym wyborem.
        // Odczytuje preferowany język z nagłówka 'Accept-Language' wysyłanego przez klienta (przeglądarkę/narzędzie API).
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("pl")); // Domyślny język, jeśli nagłówek nie jest ustawiony lub nie pasuje
        resolver.setSupportedLocales(SUPPORTED_LOCALES); // Opcjonalnie, ogranicz do wspieranych języków
        return resolver;

        // Alternatywa, jeśli chcesz kontrolować język przez parametr URL (?lang=pl)
        // lub przechowywać w sesji/ciasteczku:
        // SessionLocaleResolver slr = new SessionLocaleResolver();
        // slr.setDefaultLocale(new Locale("pl"));
        // return slr;

        // CookieLocaleResolver clr = new CookieLocaleResolver();
        // clr.setDefaultLocale(new Locale("pl"));
        // clr.setCookieName("APP_LOCALE");
        // clr.setCookieMaxAge(3600); // 1 godzina
        // return clr;
    }

    // Jeśli używasz SessionLocaleResolver lub CookieLocaleResolver i chcesz zmiany przez parametr URL:
    /*
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Pozwala na zmianę języka przez np. /api/usterki?lang=en
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    */
}