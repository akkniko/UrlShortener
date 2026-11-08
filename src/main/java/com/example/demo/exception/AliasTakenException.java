package com.example.demo.exception;

public class AliasTakenException extends ShortenerException {
    private static final long serialVersionUID = 1L;
    public AliasTakenException(String alias) { super("Alias '" + alias + "' is already taken"); }
}
