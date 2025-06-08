package pl.usterkimiejskie.usterkimiejskie.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException; // Dodaj ten import
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Metoda pomocnicza do pobierania przetłumaczonej wiadomości
    private String getMessage(String code, Object... args) {
        // Domyślny komunikat, jeśli klucz nie zostanie znaleziony (na wszelki wypadek)
        String defaultMessageIfNotFound = "Error message for code '" + code + "' not found.";
        // Jeśli args jest null, przekaż pustą tablicę, aby uniknąć NullPointerException
        Object[] nonNullArgs = (args == null || (args.length == 1 && args[0] == null)) ? new Object[0] : args;
        try {
            return messageSource.getMessage(code, nonNullArgs, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            logger.warn("Could not find message for code '{}' and locale '{}'. Returning default. Error: {}",
                    code, LocaleContextHolder.getLocale(), e.getMessage());
            // Próba zwrócenia komunikatu z oryginalnego wyjątku, jeśli to możliwe i sensowne
            if (nonNullArgs.length > 0 && nonNullArgs[0] instanceof String) {
                return (String) nonNullArgs[0]; // Jeśli pierwszy argument to string, może to być oryginalna wiadomość
            }
            return defaultMessageIfNotFound; // Ostateczny fallback
        }
    }

    // Metoda pomocnicza do pobierania ścieżki żądania
    private String getPath(WebRequest request) {
        String path = (String) request.getAttribute("jakarta.servlet.error.request_uri", WebRequest.SCOPE_REQUEST);
        if (path == null) {
            path = request.getDescription(false);
            if (path != null && path.startsWith("uri=")) {
                path = path.substring(4);
            }
        }
        return path;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        String originalMessage = ex.getMessage();
        String entityId = "";
        if (originalMessage != null) {
            String[] parts = originalMessage.split("ID: ");
            if (parts.length > 1) {
                entityId = parts[1].replaceAll("[^0-9]", "");
            }
        }

        String localizedMessage = getMessage("error.entityNotFound", entityId); // Przekazujemy entityId jako argument

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase()); // Zmieniono na bardziej standardowe
        body.put("message", localizedMessage);
        body.put("path", getPath(request));

        logger.warn("EntityNotFoundException: {} (Extracted ID: {}) on path: {}", originalMessage, entityId, getPath(request));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase()); // Zmieniono na bardziej standardowe

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String field = fieldError.getField();
                    // Używamy kodów błędów (np. "NotBlank", "Size") jako części klucza,
                    // a nazwy obiektu i pola do uzupełnienia klucza.
                    // Domyślny komunikat z adnotacji może być fallbackiem.
                    String[] codes = fieldError.getCodes(); // Np. ["NotBlank.usterkaDto.miasto", "NotBlank.miasto", "NotBlank.java.lang.String", "NotBlank"]
                    String localizedMessage = fieldError.getDefaultMessage(); // Fallback
                    if (codes != null) {
                        for (String code : codes) {
                            try {
                                localizedMessage = messageSource.getMessage(code, fieldError.getArguments(), LocaleContextHolder.getLocale());
                                break; // Znaleziono tłumaczenie, przerwij pętlę
                            } catch (Exception e) {
                                // Kontynuuj, spróbuj następnego kodu
                            }
                        }
                    }
                    if (localizedMessage == null || localizedMessage.equals(fieldError.getDefaultMessage())) {
                        // Jeśli nadal nie ma tłumaczenia lub jest to domyślny komunikat,
                        // a domyślny komunikat jest kluczem, spróbuj go użyć.
                        try {
                            localizedMessage = messageSource.getMessage(fieldError.getDefaultMessage(), fieldError.getArguments(), LocaleContextHolder.getLocale());
                        } catch (Exception e) {
                            logger.warn("Nie znaleziono klucza i18n dla błędu walidacji (kod/defaultMessage): '{}'. Używam oryginalnego komunikatu: '{}'", fieldError.getDefaultMessage(), fieldError.getDefaultMessage());
                            localizedMessage = fieldError.getDefaultMessage(); // Ostateczny fallback
                        }
                    }
                    return field + ": " + localizedMessage;
                })
                .collect(Collectors.toList());

        body.put("messages", errors);
        body.put("path", getPath(request));

        logger.warn("MethodArgumentNotValidException: Błędy walidacji na ścieżce {}: {}", getPath(request), errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handler dla UserAlreadyExistsException (jeśli go używasz)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        logger.warn("UserAlreadyExistsException na ścieżce {}: {}", getPath(request), ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict
        body.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        body.put("message", ex.getMessage()); // Możesz też to internacjonalizować
        body.put("path", getPath(request));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handler dla AuthenticationException (błędy logowania)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.warn("Błąd uwierzytelniania na ścieżce {}: {}", getPath(request), ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        // Użyj klucza i18n dla komunikatu błędu uwierzytelniania
        body.put("message", getMessage("error.authenticationFailed", "Nieprawidłowe dane logowania."));
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Ogólny handler dla innych nieprzewidzianych wyjątków
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Nieoczekiwany błąd serwera na ścieżce {}:", getPath(request), ex); // Logujemy pełny stack trace

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.put("message", getMessage("error.generic")); // Używamy przetłumaczonej ogólnej wiadomości
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}