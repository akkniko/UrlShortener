package com.example.demo.model;

import java.time.Instant;

public record Click(Long id, Long linkId, Instant clickedAt, String referrer, String userAgent) {

}
