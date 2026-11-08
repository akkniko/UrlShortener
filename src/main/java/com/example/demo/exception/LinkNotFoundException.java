package com.example.demo.exception;

public class LinkNotFoundException extends ShortenerException {
    private static final long serialVersionUID = 1L;
    public LinkNotFoundException(String code) { super("Link with code '" + code + "' was not found"); }
}
