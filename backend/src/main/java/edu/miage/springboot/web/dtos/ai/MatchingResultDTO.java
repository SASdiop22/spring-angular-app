package edu.miage.springboot.web.dtos.ai;

import java.util.List;

public class MatchingResultDTO {

    private int matchingScore;
    private List<String> strengths;
    private List<String> missingSkills;
    private String recommendation;

    // Constructeur vide (OBLIGATOIRE pour Jackson)
    public MatchingResultDTO() {
    }

    // Getters & Setters
    public int getMatchingScore() {
        return matchingScore;
    }

    public void setMatchingScore(int matchingScore) {
        this.matchingScore = matchingScore;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
