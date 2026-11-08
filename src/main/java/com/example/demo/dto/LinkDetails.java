package com.example.demo.dto;

import java.util.List;

public record LinkDetails(LinkView link, long totalClicks, List<DayStat> dailyStats) {
    public LinkDetails {
        dailyStats = List.copyOf(dailyStats);
    }
}
