package com.example.demo.service;

import java.security.SecureRandom;
import java.util.Objects;

public class CodeGenerator {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int DEFAULT_LENGTH = 7;
    private final SecureRandom random;

    public CodeGenerator() {
        this(new SecureRandom());
    }

    CodeGenerator(SecureRandom random) {
        this.random = Objects.requireNonNull(random);
    }

    public String generate() {
        return generate(DEFAULT_LENGTH);
    }

    public String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Code length must be positive");
        }
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return result.toString();
    }
}
