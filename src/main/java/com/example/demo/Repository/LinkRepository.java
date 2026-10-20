package com.example.demo.Repository;
import com.example.demo.Model.Link;

public interface LinkRepository {
    Link findById(Long id);
    Link findByOriginalURL(String originalUrl);
    Link findByCode(String code);
    Link deleteById(Long id);

}
