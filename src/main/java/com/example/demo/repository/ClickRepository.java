package com.example.demo.repository;

import com.example.demo.dto.DayStat;
import com.example.demo.model.Click;

import java.util.List;

public interface ClickRepository {
     Click save(Click click);
     long countByLinkId(Long linkId);
     List<DayStat> findDailyStatsByLinkId(Long linkId);
}
