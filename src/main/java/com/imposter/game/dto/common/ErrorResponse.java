package com.imposter.game.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String timestamp;  // Changed from Instant to String
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_INSTANT;

    // Factory methods
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now().toString())  // Use ISO string directly
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse withFieldErrors(int status, String error, String message,
                                                String path, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(Instant.now().toString())  // Use ISO string directly
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
    }
}