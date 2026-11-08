package com.example.demo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorTest {
    private final CodeGenerator generator = new CodeGenerator();

    @Test
    void generatesSevenCharactersByDefault() {
        String code = generator.generate();
        assertEquals(7, code.length());
        assertTrue(code.matches("[A-Za-z0-9]{7}"));
    }

    @Test
    void generatesRequestedLength() {
        assertEquals(12, generator.generate(12).length());
    }

    @Test
    void rejectsNonPositiveLength() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(0));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }
}
