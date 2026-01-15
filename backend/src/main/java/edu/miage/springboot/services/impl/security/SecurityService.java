package edu.miage.springboot.services.impl.security;

import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Ajoutez l'import : import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;

@Service("securityService")
public class SecurityService {
    @Autowired private CandidatRepository candidatRepository;
    @Autowired private JobOfferRepository jobOfferRepository; // Ajouter ceci
    @Autowired private ApplicationRepository applicationRepository;

    /**
     * Vérifie si l'utilisateur est le propriétaire du profil Candidat
     * Utilisé dans CandidatController
     */
    public boolean isOwner(Long candidateId) {
        if (candidateId == null) return false;
        if (hasPrivilegedRole()) return true; // Un Admin/RH peut tout voir

        String username = getConnectedUsername();
        return candidatRepository.findById(candidateId)
                .map(c -> c.getUser().getUsername().equals(username))
                .orElse(false);
    }

    // Ajoute cette méthode ou renomme l'existante
    public boolean isApplicationOwner(Long candidateId) {
        return isOwner(candidateId); // Appelle la logique de vérification de propriété
    }

    /**
     * Vérifie si l'utilisateur est le créateur de l'offre d'emploi
     * Utilisé dans JobOfferController
     */
    public boolean isJobOfferOwner(Long jobOfferId) {
        if (jobOfferId == null) return false;
        if (hasPrivilegedRole()) return true;

        String username = getConnectedUsername();
        return jobOfferRepository.findById(jobOfferId)
                .map(job -> job.getCreator().getUser().getUsername().equals(username))
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur peut accéder à une candidature (soit le candidat, soit le recruteur)
     */
    public boolean canAccessApplication(Long applicationId) {
        String username = getConnectedUsername();
        return applicationRepository.findById(applicationId)
                .map(app -> {
                    boolean isCandidatOwner = app.getCandidate().getUser().getUsername().equals(username);
                    boolean isRecruteurOwner = app.getJob().getCreator().getUser().getUsername().equals(username);
                    return isCandidatOwner || isRecruteurOwner || hasPrivilegedRole();
                })
                .orElse(false);
    }

    public boolean hasPrivilegedRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_RH"));
    }

    public boolean isEmployeAnyKind() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_RH") || a.getAuthority().equals("ROLE_EMPLOYE"));
    }

    // --- Méthodes utilitaires ---


    private String getConnectedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }


}