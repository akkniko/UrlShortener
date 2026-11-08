package com.example.demo.service;

import com.example.demo.dto.DayStat;
import com.example.demo.dto.LinkDetails;
import com.example.demo.dto.LinkView;
import com.example.demo.exception.LinkNotFoundException;
import com.example.demo.model.Link;
import com.example.demo.repository.ClickRepository;
import com.example.demo.repository.LinkRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatsService {
    private final LinkRepository linkRepository;
    private final ClickRepository clickRepository;
    private final Clock clock;

    public StatsService(LinkRepository linkRepository, ClickRepository clickRepository) {
        this(linkRepository, clickRepository, Clock.systemUTC());
    }

    StatsService(LinkRepository linkRepository, ClickRepository clickRepository, Clock clock) {
        this.linkRepository = Objects.requireNonNull(linkRepository);
        this.clickRepository = Objects.requireNonNull(clickRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public LinkDetails getStats(String code) {
        Link link = linkRepository.findByCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));

        long totalClicks = clickRepository.countByLinkId(link.getId());
        Map<LocalDate, Long> countsByDay = new HashMap<>();
        for (DayStat stat : clickRepository.findDailyStatsByLinkId(link.getId())) {
            countsByDay.put(stat.day(), stat.clicks());
        }

        LocalDate firstDay = link.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate lastDay = LocalDate.now(clock.withZone(ZoneOffset.UTC));
        if (link.getExpireAt() != null) {
            LocalDate expiryDay = link.getExpireAt().atZone(ZoneOffset.UTC).toLocalDate();
            if (expiryDay.isBefore(lastDay)) {
                lastDay = expiryDay;
            }
        }

        List<DayStat> dailyStats = new ArrayList<>();
        for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            dailyStats.add(new DayStat(day, countsByDay.getOrDefault(day, 0L)));
        }

        LinkView view = new LinkView(link.getCode(), link.getOriginalUrl(),
                link.getCreatedAt(), link.getExpireAt());
        return new LinkDetails(view, totalClicks, dailyStats);
    }
}
