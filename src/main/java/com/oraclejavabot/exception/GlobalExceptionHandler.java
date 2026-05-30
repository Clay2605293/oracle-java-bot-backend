package com.oraclejavabot.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        ex.printStackTrace();

        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("type", ex.getClass().getName());

        return response;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleIllegalState(IllegalStateException ex) {
        ex.printStackTrace();

        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("type", ex.getClass().getName());

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        ex.printStackTrace();

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        errors.put("type", ex.getClass().getName());

        return errors;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrity(DataIntegrityViolationException ex) {
        ex.printStackTrace();

        String message;

        if (ex.getMostSpecificCause() != null && ex.getMostSpecificCause().getMessage() != null) {
            message = ex.getMostSpecificCause().getMessage();
        } else if (ex.getMessage() != null) {
            message = ex.getMessage();
        } else {
            message = "Sin mensaje";
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "La operación viola una restricción de integridad en la base de datos");
        response.put("message", message);
        response.put("type", ex.getClass().getName());

        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneric(Exception ex) {
        System.out.println("🔥 GLOBAL HANDLER NUEVO EJECUTADO");
        ex.printStackTrace();

        String message = ex.getMessage() != null ? ex.getMessage() : "Sin mensaje";

        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor");
        response.put("message", message);
        response.put("type", ex.getClass().getName());

        return response;
    }
}