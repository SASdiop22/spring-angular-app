/**
 * ============================================================================
 * EXEMPLE D'INTÉGRATION FRONTEND - CONSULTATION DES CANDIDATS
 * Framework: Angular
 * ============================================================================
 */

// ============================================================================
// 1. SERVICE - applications.service.ts
// ============================================================================

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationDTO } from './models/application.model';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {

  private apiUrl = 'http://localhost:8080/api';
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  });

  constructor(private http: HttpClient) { }

  /**
   * Récupère les candidatures pour une offre d'emploi spécifique
   * Les candidats sont triés par score de correspondance décroissant
   *
   * @param jobOfferId - ID de l'offre d'emploi
   * @returns Observable<ApplicationDTO[]> - Liste des candidatures
   */
  getJobCandidates(jobOfferId: number): Observable<ApplicationDTO[]> {
    return this.http.get<ApplicationDTO[]>(
      `${this.apiUrl}/joboffers/${jobOfferId}/candidates`,
      { headers: this.headers }
    );
  }

  /**
   * Récupère une candidature spécifique
   */
  getApplication(applicationId: number): Observable<ApplicationDTO> {
    return this.http.get<ApplicationDTO>(
      `${this.apiUrl}/applications/${applicationId}`,
      { headers: this.headers }
    );
  }

  /**
   * Met à jour le statut d'une candidature
   */
  updateApplicationStatus(
    applicationId: number,
    newStatus: string,
    feedback?: string
  ): Observable<ApplicationDTO> {
    const body = { status: newStatus, feedback };
    return this.http.patch<ApplicationDTO>(
      `${this.apiUrl}/applications/${applicationId}/status`,
      body,
      { headers: this.headers }
    );
  }

  /**
   * Programme un entretien pour une candidature
   */
  scheduleInterview(
    applicationId: number,
    interviewDate: Date,
    interviewerId: number
  ): Observable<ApplicationDTO> {
    const body = {
      interviewDate: interviewDate.toISOString(),
      interviewerId
    };
    return this.http.patch<ApplicationDTO>(
      `${this.apiUrl}/applications/${applicationId}/schedule-interview`,
      body,
      { headers: this.headers }
    );
  }
}

// ============================================================================
// 2. MODÈLE - application.model.ts
// ============================================================================

export interface ApplicationDTO {
  id: number;
  candidateName: string;
  candidateId: number;
  status: string; // RECEIVED, INTERVIEW_PENDING, HIRED, etc.
  applicationDate: Date;
  createdAt: Date;
  matchingScore: number; // 0-100
  cvUrl: string;
  coverLetter: string;
  jobOfferId: number;
  jobOfferTitle: string;
  meetingDate?: Date;
  rejectionReason?: string;
  notes?: ApplicationNoteDTO[];
}

export interface ApplicationNoteDTO {
  id: number;
  content: string;
  createdAt: Date;
  author: string;
}

export interface ApplicationFilter {
  status?: string;
  minScore?: number;
  maxScore?: number;
}

// ============================================================================
// 3. COMPOSANT - job-candidates.component.ts
// ============================================================================

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApplicationService } from '../../services/application.service';
import { JobOfferService } from '../../services/job-offer.service';
import { ApplicationDTO } from '../../models/application.model';
import { JobOfferDTO } from '../../models/job-offer.model';

@Component({
  selector: 'app-job-candidates',
  templateUrl: './job-candidates.component.html',
  styleUrls: ['./job-candidates.component.scss']
})
export class JobCandidatesComponent implements OnInit {

  jobOfferId: number;
  jobOffer: JobOfferDTO;
  candidates: ApplicationDTO[] = [];
  filteredCandidates: ApplicationDTO[] = [];
  loading = false;
  error: string = null;

  // Filtres
  statusFilter = '';
  scoreFilter = 0;
  sortBy: 'score' | 'name' | 'date' = 'score';

  // Pagination
  pageSize = 10;
  currentPage = 0;

  constructor(
    private route: ActivatedRoute,
    private applicationService: ApplicationService,
    private jobOfferService: JobOfferService
  ) { }

  ngOnInit(): void {
    this.jobOfferId = +this.route.snapshot.paramMap.get('id');
    this.loadJobOffer();
    this.loadCandidates();
  }

  /**
   * Charge les informations de l'offre d'emploi
   */
  loadJobOffer(): void {
    this.jobOfferService.getJobOffer(this.jobOfferId).subscribe(
      (jobOffer) => {
        this.jobOffer = jobOffer;
      },
      (error) => {
        console.error('Erreur lors du chargement de l\'offre', error);
        this.error = 'Impossible de charger l\'offre d\'emploi';
      }
    );
  }

  /**
   * Charge les candidatures pour cette offre
   */
  loadCandidates(): void {
    this.loading = true;
    this.error = null;

    this.applicationService.getJobCandidates(this.jobOfferId).subscribe(
      (candidates) => {
        this.candidates = candidates;
        this.applyFilters();
        this.loading = false;
      },
      (error) => {
        console.error('Erreur lors du chargement des candidats', error);
        this.error = 'Impossible de charger les candidats';
        this.loading = false;
      }
    );
  }

  /**
   * Applique les filtres et le tri
   */
  applyFilters(): void {
    let filtered = [...this.candidates];

    // Filtre par statut
    if (this.statusFilter) {
      filtered = filtered.filter(c => c.status === this.statusFilter);
    }

    // Filtre par score minimum
    if (this.scoreFilter > 0) {
      filtered = filtered.filter(c => c.matchingScore >= this.scoreFilter);
    }

    // Tri
    switch (this.sortBy) {
      case 'score':
        filtered.sort((a, b) => b.matchingScore - a.matchingScore);
        break;
      case 'name':
        filtered.sort((a, b) => a.candidateName.localeCompare(b.candidateName));
        break;
      case 'date':
        filtered.sort((a, b) =>
          new Date(b.applicationDate).getTime() - new Date(a.applicationDate).getTime()
        );
        break;
    }

    this.filteredCandidates = filtered;
    this.currentPage = 0;
  }

  /**
   * Retourne les candidats pour la page actuelle
   */
  get paginatedCandidates(): ApplicationDTO[] {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredCandidates.slice(start, end);
  }

  /**
   * Nombre total de pages
   */
  get totalPages(): number {
    return Math.ceil(this.filteredCandidates.length / this.pageSize);
  }

  /**
   * Retourne la couleur de la badge du score
   */
  getScoreBadgeClass(score: number): string {
    if (score >= 90) return 'badge-success'; // Excellent
    if (score >= 75) return 'badge-info';    // Très bon
    if (score >= 60) return 'badge-primary'; // Bon
    if (score >= 40) return 'badge-warning'; // Modéré
    return 'badge-danger';                   // Mauvais
  }

  /**
   * Retourne le libellé du score
   */
  getScoreLabel(score: number): string {
    if (score >= 90) return 'Excellent match';
    if (score >= 75) return 'Très bon match';
    if (score >= 60) return 'Bon match';
    if (score >= 40) return 'Match modéré';
    return 'Mauvais match';
  }

  /**
   * Retourne la couleur du badge de statut
   */
  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'RECEIVED':
        return 'badge-secondary';
      case 'INTERVIEW_PENDING':
        return 'badge-info';
      case 'TECHNICAL_TEST_PENDING':
        return 'badge-warning';
      case 'OFFER_PENDING':
        return 'badge-primary';
      case 'HIRED':
        return 'badge-success';
      case 'REJECTED':
        return 'badge-danger';
      default:
        return 'badge-dark';
    }
  }

  /**
   * Ouvre le formulaire de programmation d'entretien
   */
  openScheduleForm(candidate: ApplicationDTO): void {
    // À implémenter selon votre design
    console.log('Programmer un entretien avec', candidate.candidateName);
  }

  /**
   * Programme un entretien
   */
  scheduleInterview(
    candidate: ApplicationDTO,
    interviewDate: Date,
    interviewerId: number
  ): void {
    this.applicationService.scheduleInterview(
      candidate.id,
      interviewDate,
      interviewerId
    ).subscribe(
      (updatedApplication) => {
        // Mettre à jour la candidature dans la liste
        const index = this.candidates.findIndex(c => c.id === updatedApplication.id);
        if (index > -1) {
          this.candidates[index] = updatedApplication;
          this.applyFilters();
        }
        console.log('Entretien programmé avec succès');
      },
      (error) => {
        console.error('Erreur lors de la programmation', error);
      }
    );
  }

  /**
   * Rejette un candidat
   */
  rejectCandidate(candidate: ApplicationDTO, reason: string): void {
    this.applicationService.updateApplicationStatus(
      candidate.id,
      'REJECTED',
      reason
    ).subscribe(
      (updatedApplication) => {
        const index = this.candidates.findIndex(c => c.id === updatedApplication.id);
        if (index > -1) {
          this.candidates[index] = updatedApplication;
          this.applyFilters();
        }
        console.log('Candidat rejeté');
      },
      (error) => {
        console.error('Erreur lors du rejet', error);
      }
    );
  }

  /**
   * Télécharge le CV
   */
  downloadCV(candidate: ApplicationDTO): void {
    window.open(candidate.cvUrl, '_blank');
  }

  /**
   * Copie l'email du candidat (depuis le profil complet)
   */
  copyCandidateEmail(candidate: ApplicationDTO): void {
    // À implémenter selon votre structure de données
    console.log('Email copié');
  }
}

// ============================================================================
// 4. TEMPLATE HTML - job-candidates.component.html
// ============================================================================

/*
<div class="job-candidates-container">
  <!-- En-tête avec infos de l'offre -->
  <div class="header-section" *ngIf="jobOffer">
    <h2>{{ jobOffer.title }}</h2>
    <p class="company">{{ jobOffer.companyName }} | {{ jobOffer.location }}</p>
    <p class="candidate-count">
      <strong>{{ filteredCandidates.length }}</strong> candidat(s)
      <span *ngIf="candidates.length !== filteredCandidates.length" class="text-muted">
        ({{ candidates.length }} au total)
      </span>
    </p>
  </div>

  <!-- Zone de chargement -->
  <div *ngIf="loading" class="loading-spinner">
    <div class="spinner-border" role="status">
      <span class="sr-only">Chargement...</span>
    </div>
  </div>

  <!-- Message d'erreur -->
  <div *ngIf="error" class="alert alert-danger alert-dismissible fade show" role="alert">
    {{ error }}
    <button type="button" class="btn-close" (click)="error = null"></button>
  </div>

  <!-- Filtres et tri -->
  <div class="filters-section" *ngIf="!loading && candidates.length > 0">
    <div class="row g-3">
      <div class="col-md-3">
        <label class="form-label">Statut</label>
        <select class="form-select" [(ngModel)]="statusFilter" (change)="applyFilters()">
          <option value="">Tous les statuts</option>
          <option value="RECEIVED">Reçu</option>
          <option value="INTERVIEW_PENDING">En attente d'entretien</option>
          <option value="TECHNICAL_TEST_PENDING">Test technique</option>
          <option value="OFFER_PENDING">Offre proposée</option>
          <option value="HIRED">Embauché</option>
          <option value="REJECTED">Rejeté</option>
        </select>
      </div>

      <div class="col-md-3">
        <label class="form-label">Score minimum</label>
        <input type="range" class="form-range" min="0" max="100" step="10"
               [(ngModel)]="scoreFilter" (change)="applyFilters()">
        <small class="text-muted">{{ scoreFilter }}%</small>
      </div>

      <div class="col-md-3">
        <label class="form-label">Trier par</label>
        <select class="form-select" [(ngModel)]="sortBy" (change)="applyFilters()">
          <option value="score">Score décroissant</option>
          <option value="name">Nom A-Z</option>
          <option value="date">Date récente</option>
        </select>
      </div>

      <div class="col-md-3">
        <label class="form-label">&nbsp;</label>
        <button class="btn btn-outline-secondary w-100" (click)="loadCandidates()">
          Actualiser
        </button>
      </div>
    </div>
  </div>

  <!-- Liste des candidats -->
  <div class="candidates-list" *ngIf="!loading && paginatedCandidates.length > 0">
    <div class="candidate-card" *ngFor="let candidate of paginatedCandidates">
      <div class="row align-items-center">
        <!-- Colonne 1: Infos candidat -->
        <div class="col-md-4">
          <h5 class="mb-1">{{ candidate.candidateName }}</h5>
          <small class="text-muted">ID: {{ candidate.candidateId }}</small>
          <p class="mb-0">
            <span class="badge" [ngClass]="getStatusBadgeClass(candidate.status)">
              {{ candidate.status }}
            </span>
          </p>
        </div>

        <!-- Colonne 2: Score et match -->
        <div class="col-md-3 text-center">
          <div class="score-display">
            <div class="score-circle" [ngClass]="getScoreBadgeClass(candidate.matchingScore)">
              {{ candidate.matchingScore }}%
            </div>
            <small>{{ getScoreLabel(candidate.matchingScore) }}</small>
          </div>
        </div>

        <!-- Colonne 3: Dates -->
        <div class="col-md-2">
          <small class="text-muted">
            Postulé: {{ candidate.applicationDate | date: 'dd/MM/yyyy' }}
          </small>
          <br>
          <small class="text-muted" *ngIf="candidate.meetingDate">
            Entretien: {{ candidate.meetingDate | date: 'dd/MM/yyyy HH:mm' }}
          </small>
        </div>

        <!-- Colonne 4: Actions -->
        <div class="col-md-3 text-end">
          <div class="btn-group-vertical w-100">
            <button class="btn btn-sm btn-info" (click)="openScheduleForm(candidate)">
              📅 Entretien
            </button>
            <button class="btn btn-sm btn-success" [routerLink]="['/applications', candidate.id]">
              👁️ Détails
            </button>
            <button class="btn btn-sm btn-danger" (click)="rejectCandidate(candidate, 'Non sélectionné')">
              ❌ Rejeter
            </button>
          </div>
        </div>
      </div>

      <!-- Détails additionnels (optionnel) -->
      <div class="candidate-details mt-2" *ngIf="false">
        <p class="mb-1">{{ candidate.coverLetter }}</p>
        <a href="{{ candidate.cvUrl }}" target="_blank" class="btn btn-sm btn-outline-primary">
          📄 Télécharger CV
        </a>
      </div>
    </div>
  </div>

  <!-- Pagination -->
  <nav class="pagination-section" *ngIf="totalPages > 1">
    <ul class="pagination justify-content-center">
      <li class="page-item" [class.disabled]="currentPage === 0">
        <a class="page-link" href="#" (click)="currentPage = 0">Première</a>
      </li>
      <li class="page-item" [class.disabled]="currentPage === 0">
        <a class="page-link" href="#" (click)="currentPage--" *ngIf="currentPage > 0">Précédente</a>
      </li>
      <li class="page-item" *ngFor="let i of [].constructor(totalPages); let idx = index"
          [class.active]="idx === currentPage">
        <a class="page-link" href="#" (click)="currentPage = idx">{{ idx + 1 }}</a>
      </li>
      <li class="page-item" [class.disabled]="currentPage === totalPages - 1">
        <a class="page-link" href="#" (click)="currentPage++" *ngIf="currentPage < totalPages - 1">Suivante</a>
      </li>
      <li class="page-item" [class.disabled]="currentPage === totalPages - 1">
        <a class="page-link" href="#" (click)="currentPage = totalPages - 1">Dernière</a>
      </li>
    </ul>
  </nav>

  <!-- Message si aucun candidat -->
  <div class="empty-state" *ngIf="!loading && candidates.length === 0">
    <p class="text-muted">Aucun candidat pour cette offre</p>
  </div>
</div>
*/

// ============================================================================
// 5. STYLES - job-candidates.component.scss
// ============================================================================

/*
.job-candidates-container {
  padding: 20px;

  .header-section {
    border-bottom: 2px solid #f0f0f0;
    padding-bottom: 20px;
    margin-bottom: 30px;

    h2 {
      margin-bottom: 10px;
    }

    .company {
      color: #666;
      margin-bottom: 10px;
    }

    .candidate-count {
      font-weight: bold;
      color: #007bff;
    }
  }

  .filters-section {
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 30px;
  }

  .candidates-list {
    .candidate-card {
      background: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 15px;
      transition: box-shadow 0.3s ease;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .score-circle {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 18px;
        color: white;
        margin: 0 auto;

        &.badge-success { background-color: #28a745; }
        &.badge-info { background-color: #17a2b8; }
        &.badge-primary { background-color: #007bff; }
        &.badge-warning { background-color: #ffc107; color: #333; }
        &.badge-danger { background-color: #dc3545; }
      }
    }
  }

  .pagination-section {
    margin-top: 30px;
  }

  .empty-state {
    text-align: center;
    padding: 40px;
  }
}
*/

