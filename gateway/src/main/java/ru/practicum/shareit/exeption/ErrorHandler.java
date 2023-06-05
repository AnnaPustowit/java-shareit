package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotFoundException(final MethodArgumentNotValidException exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleValidationException(final Throwable exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
