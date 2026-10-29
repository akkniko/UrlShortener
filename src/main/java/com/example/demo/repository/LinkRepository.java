package com.example.demo.repository;
import com.example.demo.model.Link;

public interface LinkRepository {
    Link findById(Long id);
    Link findByOriginalURL(String originalUrl);
    Link findByCode(String code);
    Link deleteById(Long id);
}
