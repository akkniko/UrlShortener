package com.example.demo.dto;

import java.time.Instant;

public record LinkView(String shortUrl, String originalUrl,
                       Instant createdAt, Instant expireAt,
                       int numClicks ) {

}
