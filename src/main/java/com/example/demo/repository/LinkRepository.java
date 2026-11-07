package com.example.demo.repository;
import com.example.demo.model.Link;

import java.util.Optional;

public interface LinkRepository {
    Optional findById(Long id);
    Optional findByOriginalURL(String originalUrl);
    Optional findByCode(String code);
    Link deleteById(Long id);
    Link save(String originalUrl);
}
