package com.example.demo.repository;

import com.example.demo.model.Link;

import java.util.Optional;

public interface LinkRepository {
    Optional<Link> findByCode(String code);
    Link save(Link link);
}
