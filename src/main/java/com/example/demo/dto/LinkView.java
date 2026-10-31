package com.example.demo.dto;

import java.time.Instant;

public record LinkView(String code, String originalUrl,
                       Instant createdAt, Instant expireAt) {

}
