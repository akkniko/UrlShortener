package com.example.demo.exception;

public class LinkExpiredException extends ShortenerException {
    private static final long serialVersionUID = 1L;
    public LinkExpiredException(String code) { super("Link with code '" + code + "' has expired"); }
}
