package com.example.demo.model;

import java.time.Instant;
import java.util.Objects;

public class Link {
    private Long id;
    private String originalUrl;
    private String code;
    private Instant createdAt;
    private Instant expireAt;

    public Link(String originalUrl) {
        this(null, originalUrl, null, Instant.now(), null);
    }

    public Link(Long id, String originalUrl, String code, Instant createdAt, Instant expireAt) {
        this.id = id;
        this.originalUrl = Objects.requireNonNull(originalUrl, "originalUrl");
        this.code = code;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.expireAt = expireAt;
    }

    public Long getId() { return id; }
    public String getOriginalUrl() { return originalUrl; }
    public String getCode() { return code; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpireAt() { return expireAt; }

    public void setId(Long id) { this.id = id; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = Objects.requireNonNull(originalUrl); }
    public void setCode(String code) { this.code = code; }
    public void setExpireAt(Instant expireAt) { this.expireAt = expireAt; }
}
