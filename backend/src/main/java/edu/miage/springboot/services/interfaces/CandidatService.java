package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.users.CandidatDTO;
import java.util.List;

/**
 * Service gérant la logique métier des candidats.
 */
public interface CandidatService {

    /**
     * Récupère la liste de tous les candidats (réservé RH/Admin).
     */
    List<CandidatDTO> findAll();

    /**
     * Récupère un candidat par son identifiant unique.
     * @param id L'identifiant (partagé avec UserEntity)
     */
    CandidatDTO findById(Long id);

    /**
     * Met à jour les informations personnelles du candidat.
     * @param id L'identifiant du profil à modifier
     * @param dto Les nouvelles données (nom, prénom, téléphone, ville)
     */
    CandidatDTO updateProfile(Long id, CandidatDTO dto);

    /**
     * Spécification 3.A : Renouvelle le consentement RGPD.
     * Met à jour la date de consentement à 'maintenant' pour repartir sur un cycle de 2 ans.
     */
    void renewConsent(Long id);

    /**
     * Supprime un profil candidat et son compte utilisateur associé.
     */
    void deleteCandidat(Long id);

    CandidatDTO findByUsername(String username);
}