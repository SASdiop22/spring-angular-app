package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.entities.users.UserTypeEnum;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.repositories.users.UserRoleRepository;
import edu.miage.springboot.services.impl.offers.matching.MatchingServiceImpl;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private JobOfferRepository jobOfferRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private MatchingServiceImpl matchingService;

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatusEnum status, String reason) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        // Spec 4.B : Gestion du motif de rejet
        if (status == ApplicationStatusEnum.REJECTED) {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("Un motif de rejet est obligatoire pour refuser une candidature.");
            }
            app.setRejectionReason(reason);
            sendRejectionEmail(app.getCandidate().getUser().getEmail(), reason);
        }

        // Sécurité : Vérifier si un entretien est planifié pour les statuts avancés
        if (isStatusRequiringMeeting(status) && app.getMeetingDate() == null) {
            throw new IllegalStateException("Impossible de passer à ce statut sans avoir planifié d'entretien.");
        }

        app.setCurrentStatus(status);
        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public void hireCandidate(Long applicationId) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        if (app.getCurrentStatus() == ApplicationStatusEnum.HIRED) {
            throw new IllegalStateException("Ce candidat a déjà été embauché.");
        }

        UserEntity user = app.getCandidate().getUser();

        // 1. Création du profil Employé (Spec 5)
        EmployeEntity newEmployee = new EmployeEntity();
        newEmployee.setUser(user);
        newEmployee.setPoste(app.getJob().getTitle());
        newEmployee.setDepartement(app.getJob().getDepartment());

        // 2. Assignation du Référent/Manager (Spec 5)
        // Par défaut, le créateur de l'offre (le demandeur) devient son manager
        newEmployee.setReferent(app.getJob().getCreator());

        // 3. Mise à jour de l'utilisateur (Type et Rôles)
        user.setUserType(UserTypeEnum.EMPLOYE);
        userRoleRepository.findByName("ROLE_EMPLOYE")
                .ifPresent(role -> user.getRoles().add(role));

        // 4. Mise à jour des statuts (Candidature et Offre)
        app.setCurrentStatus(ApplicationStatusEnum.HIRED);

        // Si l'offre ne concernait qu'un poste, on peut la fermer
        JobOfferEntity job = app.getJob();
        job.setStatus(JobStatusEnum.CLOSED);

        employeRepository.save(newEmployee);
        userRepository.save(user);
        applicationRepository.save(app);
        jobOfferRepository.save(job);
    }

    @Transactional
    public ApplicationDTO scheduleInterview(Long applicationId, LocalDateTime date, Long interviewerId, String location) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        EmployeEntity interviewer = employeRepository.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer introuvable"));

        app.setMeetingDate(date);
        app.setInterviewer(interviewer);
        app.setMeetingLocation(location);
        app.setCurrentStatus(ApplicationStatusEnum.INTERVIEW_PENDING);

        return applicationMapper.toDto(applicationRepository.save(app));
    }


    @Override
    @Transactional
    public ApplicationDTO apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter) {
        // 1. Vérifications d'existence
        JobOfferEntity job = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable"));

        CandidatEntity candidate = candidatRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable"));

        // 2. Vérifier si le candidat n'a pas déjà postulé (Évite les doublons)
        if (applicationRepository.existsByJobIdAndCandidateId(jobOfferId, candidateId)) {
            throw new IllegalStateException("Vous avez déjà postulé à cette offre.");
        }

        // 3. Création de la candidature
        ApplicationEntity app = new ApplicationEntity();
        app.setJob(job);
        app.setCandidate(candidate);
        app.setCvUrl(cvUrl);
        app.setCoverLetter(coverLetter);
        app.setCurrentStatus(ApplicationStatusEnum.RECEIVED);
        app.setCreatedAt(LocalDateTime.now());

        // 4. Calcul automatique du score de matching (Spécification 4.A)
        // Ici, on peut simuler ou appeler une logique de comparaison de mots-clés
        app.setMatchingScore(matchingService.calculateMatchScore(job, candidate));

        return applicationMapper.toDto(applicationRepository.save(app));
    }



    // Dans ApplicationServiceImpl.java
    @Transactional
    @Override
    public ApplicationDTO updateStatus(Long id, ApplicationStatusEnum status) {

        ApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        if (status == ApplicationStatusEnum.HIRED && app.getCandidate().isArchived()) {
            throw new RuntimeException("Ce candidat a déjà été recruté et son profil est archivé.");
        }

        app.setCurrentStatus(status);

        if (status == ApplicationStatusEnum.HIRED) {
            finalizeHiring(app);
        }

        return applicationMapper.toDto(applicationRepository.save(app));
    }
    private void finalizeHiring(ApplicationEntity app) {
        UserEntity user = app.getCandidate().getUser();

        // 1. Changement de rôle
        user.setUserType(UserTypeEnum.EMPLOYE);

        // 2. Création du profil employé s'il n'existe pas
        EmployeEntity newEmployee = user.getEmployeProfile();
        if (newEmployee == null) {
            newEmployee = new EmployeEntity();
            newEmployee.setUser(user);
        }

        JobOfferEntity job = app.getJob();
        job.setStatus(JobStatusEnum.FILLED);
        jobOfferRepository.save(job);
        newEmployee.setPoste(job.getTitle());
        newEmployee.setDepartement(job.getDepartment());

        // 3. LIEN HIÉRARCHIQUE : Le manager est le créateur de l'offre
        // On récupère l'employé (demandeur) qui a créé le poste
        EmployeEntity manager = app.getJob().getCreator();
        newEmployee.setReferent(manager);

        CandidatEntity candidatProfile = app.getCandidate();
        candidatProfile.setArchived(true);


        candidatRepository.save(candidatProfile);
        employeRepository.save(newEmployee);
        userRepository.save(user);
    }

    @Override
    public ApplicationDTO findById(Long id) {
        return applicationMapper.toDto(applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable")));
    }

    @Transactional
    @Override
    public ApplicationDTO scheduleInterview(Long applicationId, LocalDateTime date, Long interviewerId) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        EmployeEntity interviewer = employeRepository.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer introuvable"));

        // Mise à jour des infos
        app.setMeetingDate(date);
        app.setInterviewer(interviewer);
        app.setCurrentStatus(ApplicationStatusEnum.INTERVIEW_PENDING);

        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    public List<ApplicationDTO> findAll() {
        return applicationMapper.toDtos(applicationRepository.findAll());
    }

    @Override
    public List<ApplicationDTO> findByCandidateId(Long candidateId) {
        return applicationMapper.toDtos(applicationRepository.findByCandidateId(candidateId));
    }

    private void sendRejectionEmail(String email, String reason) {
        System.out.println("E-MAIL AUTO : " + email + " rejeté pour : " + reason);
    }



    private boolean isStatusRequiringMeeting(ApplicationStatusEnum status) {
        return status == ApplicationStatusEnum.INTERVIEW_PENDING ||
                status == ApplicationStatusEnum.TECHNICAL_TEST_PENDING ||
                status == ApplicationStatusEnum.OFFER_PENDING;
    }

}