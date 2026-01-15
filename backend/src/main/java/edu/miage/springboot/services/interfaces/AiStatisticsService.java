package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.ai.AiStatisticsDTO;

public interface AiStatisticsService {

    AiStatisticsDTO globalStats();

    AiStatisticsDTO statsByJobOffer(Long jobOfferId);
}
