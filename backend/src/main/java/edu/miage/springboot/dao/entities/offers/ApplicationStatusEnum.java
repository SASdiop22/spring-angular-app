package edu.miage.springboot.dao.entities.offers;

/**
 * Statuts conformes aux spécifications fonctionnelles 4.B et 5.
 */
public enum ApplicationStatusEnum {
    // Phase 1 : Réception
    RECEIVED,             // Candidature reçue

    // Phase 2 : Évaluation (Nécessitent une date de RDV)
    INTERVIEW_PENDING,    // Étudiée - en attente d'un entretien
    TECHNICAL_TEST_PENDING, // En attente d'un test technique

    // Phase 3 : Présélection
    SHORTLISTED,          // Présélectionnée
    REJECTED,             // Rejetée (Nécessite un motif de rejet)

    // Phase 4 : Conclusion (Nécessite une date de RDV pour la signature/offre)
    OFFER_PENDING,        // Tentative de recrutement - en attente
    OFFER_DECLINED,       // Tentative de recrutement - échouée

    // Phase 5 : Finalisation
    HIRED                 // Embauche effectuée (Déclenche le onboarding)
}