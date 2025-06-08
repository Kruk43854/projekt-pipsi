package pl.usterkimiejskie.usterkimiejskie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Do wczytywania wartości z application.properties
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    // Opcjonalnie: Wczytaj domyślny adres "Od" z application.properties
    @Value("${spring.mail.from:noreply@usterkimiejskie.example.com}") // Drugi argument to wartość domyślna
    private String defaultFromAddress;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Wysyła prostą wiadomość e-mail.
     *
     * @param to      robert.kuzba@studenci.collegiumwitelona.pl
     * @param subject robert.kuzba@studenci.collegiumwitelona.pl
     * @param text    robert.kuzba@studenci.collegiumwitelona.pl
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(defaultFromAddress); // Używamy adresu "Od"
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            logger.info("Pomyślnie wysłano e-mail do: {} z tematem: {}", to, subject);
        } catch (MailException e) {
            logger.error("Błąd podczas wysyłania e-maila do: {} z tematem: {}", to, subject, e);
            // Możesz tu rzucić niestandardowy wyjątek lub obsłużyć błąd inaczej
        }
    }
}