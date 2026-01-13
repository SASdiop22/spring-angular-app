package edu.miage.springboot.dao.entities;

public enum JobStatusEnum {
    DRAFT,      // Créé par l'employé (Brouillon)
    PENDING,    // En attente de validation RH
    OPEN,       // Publié et visible (Ouvert)
    CLOSED,     // Masqué du public (Fermé)
    FILLED      // Embauche effectuée (Archivé)
}