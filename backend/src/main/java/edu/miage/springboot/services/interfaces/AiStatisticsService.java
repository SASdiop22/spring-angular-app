package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.AiStatisticsDTO;

public interface AiStatisticsService {

    AiStatisticsDTO globalStats();

    AiStatisticsDTO statsByJobOffer(Long jobOfferId);
}
