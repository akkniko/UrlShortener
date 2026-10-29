package com.example.demo.dto;

import java.time.Instant;

public record CreateLinkRequest(String originalUrl, String alias, Instant expireAt) {


}
