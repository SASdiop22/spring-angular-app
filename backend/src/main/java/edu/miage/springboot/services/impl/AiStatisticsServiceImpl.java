package edu.miage.springboot.services.impl;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.miage.springboot.dao.entities.AiAnalysisResultEntity;
import edu.miage.springboot.dao.repositories.AiAnalysisResultRepository;
import edu.miage.springboot.services.interfaces.AiStatisticsService;
import edu.miage.springboot.web.dtos.AiStatisticsDTO;


@Service
public class AiStatisticsServiceImpl implements AiStatisticsService {

    private final AiAnalysisResultRepository repository;

    public AiStatisticsServiceImpl(AiAnalysisResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public AiStatisticsDTO globalStats() {
        List<AiAnalysisResultEntity> all = repository.findAll();
        return computeStats(all);
    }

    @Override
    public AiStatisticsDTO statsByJobOffer(Long jobOfferId) {
        List<AiAnalysisResultEntity> list =
                repository.findByJobOfferId(jobOfferId);
        return computeStats(list);
    }

    private AiStatisticsDTO computeStats(List<AiAnalysisResultEntity> list) {

        AiStatisticsDTO dto = new AiStatisticsDTO();
        dto.setTotalAnalyses(list.size());

        if (list.isEmpty()) {
            dto.setAverageScore(0);
            dto.setMostMissingSkills(Map.of());
            return dto;
        }

        double avg = list.stream()
                .mapToInt(AiAnalysisResultEntity::getMatchingScore)
                .average()
                .orElse(0);

        dto.setAverageScore(avg);

        Map<String, Long> missingSkills =
                list.stream()
                    .flatMap(r -> Arrays.stream(
                        r.getMissingSkills().split(",")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.groupingBy(
                        s -> s, Collectors.counting()));

        dto.setMostMissingSkills(missingSkills);

        return dto;
    }
}

