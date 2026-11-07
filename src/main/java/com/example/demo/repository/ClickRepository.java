package com.example.demo.repository;

import com.example.demo.model.Click;

import java.time.Instant;

public interface ClickRepository {
     int getNumClicks(Long id);
     Instant getTimeClick(Long id);
     Click save(Click c);
     int sumClickByDay(int day);
}
