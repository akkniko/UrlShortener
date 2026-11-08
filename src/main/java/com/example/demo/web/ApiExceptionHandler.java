package com.example.demo.web;

import com.example.demo.exception.AliasTakenException;
import com.example.demo.exception.InvalidUrlException;
import com.example.demo.exception.LinkExpiredException;
import com.example.demo.exception.LinkNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;

public class ApiExceptionHandler {
    private final ObjectMapper objectMapper;

    public ApiExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(Exception exception, HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        Throwable cause = unwrapServletException(exception);
        int status;
        if (cause instanceof InvalidUrlException || cause instanceof IllegalArgumentException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else if (cause instanceof LinkNotFoundException) {
            status = HttpServletResponse.SC_NOT_FOUND;
        } else if (cause instanceof AliasTakenException) {
            status = HttpServletResponse.SC_CONFLICT;
        } else if (cause instanceof LinkExpiredException) {
            status = HttpServletResponse.SC_GONE;
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        if (response.isCommitted()) {
            return;
        }
        response.resetBuffer();
        response.setStatus(status);
        response.setContentType("application/json");
        String message = status == 500 ? "Internal server error" : cause.getMessage();
        objectMapper.writeValue(response.getOutputStream(), new ApiError(status, message,
                request.getRequestURI(), Instant.now()));
    }

    private Throwable unwrapServletException(Throwable throwable) {
        Throwable current = throwable;
        while (current instanceof ServletException && current.getCause() != null
                && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    public record ApiError(int status, String error, String path, Instant timestamp) { }
}
