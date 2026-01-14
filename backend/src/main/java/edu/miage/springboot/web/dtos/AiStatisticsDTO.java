package edu.miage.springboot.web.dtos;

import java.util.Map;

public class AiStatisticsDTO {

    private double averageScore;
    private long totalAnalyses;
    private Map<String, Long> mostMissingSkills;

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public long getTotalAnalyses() {
        return totalAnalyses;
    }

    public void setTotalAnalyses(long totalAnalyses) {
        this.totalAnalyses = totalAnalyses;
    }

    public Map<String, Long> getMostMissingSkills() {
        return mostMissingSkills;
    }

    public void setMostMissingSkills(Map<String, Long> mostMissingSkills) {
        this.mostMissingSkills = mostMissingSkills;
    }
}
