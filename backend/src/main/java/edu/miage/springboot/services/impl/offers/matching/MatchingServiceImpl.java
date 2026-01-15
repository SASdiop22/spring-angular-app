package edu.miage.springboot.services.impl.offers.matching;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.services.interfaces.MatchingService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchingServiceImpl implements MatchingService {

    // Calcule le % de correspondance entre l'offre et le candidat
    public Integer calculateMatchScore(JobOfferEntity offer, CandidatEntity candidate) {
        List<String> requiredSkills = offer.getSkillsRequired();
        List<String> candidateSkills = candidate.getSkills(); // Nécessite l'ajout fait dans UserEntity

        if (requiredSkills == null || requiredSkills.isEmpty()) return 100;
        if (candidateSkills == null || candidateSkills.isEmpty()) return 0;

        Set<String> normalizedCandidateSkills = candidateSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        long matchCount = requiredSkills.stream()
                .map(String::toLowerCase)
                .filter(normalizedCandidateSkills::contains)
                .count();

        return (int) ((double) matchCount / requiredSkills.size() * 100);
    }
}