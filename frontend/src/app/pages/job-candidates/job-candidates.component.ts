import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationService } from '../../services/application.service';
import { JobOfferService } from '../../services/job-offer.service';
import { AuthService } from '../../services/auth.service';
import type { Application } from '../../models/Application';
import type { JobOffer } from '../../models/JobOffer';

@Component({
  selector: 'app-job-candidates',
  templateUrl: './job-candidates.component.html',
  styleUrls: ['./job-candidates.component.scss']
})
export class JobCandidatesComponent implements OnInit {

  jobOfferId: number;
  jobOffer: JobOffer | null = null;
  candidates: Application[] = [];
  filteredCandidates: Application[] = [];
  loading = false;
  error: string | null = null;

  // Filtres
  statusFilter = '';
  scoreFilter = 0;
  sortBy: 'score' | 'name' | 'date' = 'score';

  // Pagination
  pageSize = 10;
  currentPage = 0;

  // Rôle
  isRH = false;
  isAdmin = false;

  // Modal pour changer le statut
  showStatusModal = false;
  selectedApplication: Application | null = null;
  newStatus: string = '';
  statusUpdateLoading = false;
  statusUpdateError: string | null = null;

  // Statuts disponibles
  availableStatuses = [
    { value: 'RECEIVED', label: 'Reçue' },
    { value: 'INTERVIEW_PENDING', label: 'Entretien en attente' },
    { value: 'TECHNICAL_TEST_PENDING', label: 'Test technique en attente' },
    { value: 'SHORTLISTED', label: 'Présélectionnée' },
    { value: 'REJECTED', label: 'Rejeté' },
    { value: 'OFFER_PENDING', label: 'Offre proposée' },
    { value: 'OFFER_DECLINED', label: 'Offre déclinée' },
    { value: 'HIRED', label: 'Embauché' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private applicationService: ApplicationService,
    private jobOfferService: JobOfferService,
    private authService: AuthService
  ) {
    this.jobOfferId = 0;
  }

  ngOnInit(): void {
    this.jobOfferId = +this.route.snapshot.paramMap.get('id')!;
    this.isRH = this.authService.isRH();
    this.isAdmin = this.authService.isAdmin();

    this.loadJobOffer();
    this.loadCandidates();
  }

  /**
   * Charge les informations de l'offre d'emploi
   */
  loadJobOffer(): void {
    this.jobOfferService.getById(this.jobOfferId).subscribe(
      (jobOffer: any) => {
        this.jobOffer = jobOffer;
      },
      (error: any) => {
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

    this.applicationService.getApplicationsByJobOffer(this.jobOfferId).subscribe(
      (candidates: Application[]) => {
        this.candidates = candidates;
        this.applyFilters();
        this.loading = false;
      },
      (error: any) => {
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
      filtered = filtered.filter(c => (c.matchingScore || 0) >= this.scoreFilter);
    }

    // Tri
    switch (this.sortBy) {
      case 'score':
        filtered.sort((a, b) => (b.matchingScore || 0) - (a.matchingScore || 0));
        break;
      case 'name':
        filtered.sort((a, b) => (a.candidateName || '').localeCompare(b.candidateName || ''));
        break;
      case 'date':
        filtered.sort((a, b) => {
          const dateA = new Date(b.applicationDate || 0).getTime();
          const dateB = new Date(a.applicationDate || 0).getTime();
          return dateA - dateB;
        });
        break;
    }

    this.filteredCandidates = filtered;
    this.currentPage = 0;
  }

  /**
   * Retourne les candidats pour la page actuelle
   */
  get paginatedCandidates(): Application[] {
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
   * Retourne la couleur du badge du score
   */
  getScoreBadgeClass(score: number | null | undefined): string {
    if (!score) return 'badge-secondary';
    if (score >= 90) return 'badge-success';
    if (score >= 75) return 'badge-info';
    if (score >= 60) return 'badge-primary';
    if (score >= 40) return 'badge-warning';
    return 'badge-danger';
  }

  /**
   * Retourne le libellé du score
   */
  getScoreLabel(score: number | null | undefined): string {
    if (!score) return 'Non évalué';
    if (score >= 90) return 'Excellent match';
    if (score >= 75) return 'Très bon match';
    if (score >= 60) return 'Bon match';
    if (score >= 40) return 'Match modéré';
    return 'Mauvais match';
  }

  /**
   * Retourne le libellé du statut
   */
  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'RECEIVED': 'Reçue',
      'INTERVIEW_PENDING': 'Entretien en attente',
      'TECHNICAL_TEST_PENDING': 'Test technique',
      'OFFER_PENDING': 'Offre proposée',
      'HIRED': 'Embauché',
      'REJECTED': 'Rejeté'
    };
    return statusMap[status] || status;
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
   * Télécharge le CV du candidat
   */
  downloadCV(candidate: Application): void {
    if (!candidate.cvUrl) {
      return;
    }

    // Si c'est une URL complète (commence par http), ouvrir directement
    if (candidate.cvUrl.startsWith('http')) {
      window.open(candidate.cvUrl, '_blank');
      return;
    }

    // Sinon, créer un lien de téléchargement via l'API backend
    const downloadUrl = `http://localhost:8080/api/files/download/${encodeURIComponent(candidate.cvUrl)}`;

    // Créer un lien temporaire et le cliquer
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = candidate.cvUrl;
    link.style.display = 'none';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  /**
   * Télécharge la lettre de motivation du candidat
   */
  downloadCoverLetter(candidate: Application): void {
    if (!candidate.coverLetter) {
      return;
    }

    // Si c'est une URL complète (commence par http), ouvrir directement
    if (candidate.coverLetter.startsWith('http')) {
      window.open(candidate.coverLetter, '_blank');
      return;
    }

    // Sinon, créer un lien de téléchargement via l'API backend
    const downloadUrl = `http://localhost:8080/api/files/download/${encodeURIComponent(candidate.coverLetter)}`;

    // Créer un lien temporaire et le cliquer
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = candidate.coverLetter;
    link.style.display = 'none';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }


  /**
   * Revenir à la liste des offres
   */
  goBack(): void {
    this.router.navigate(['/job-offers']);
  }

  /**
   * Actualise les candidats
   */
  refresh(): void {
    this.loadCandidates();
  }

  /**
   * Ouvre la modale pour changer le statut d'une candidature
   */
  openStatusModal(application: Application): void {
    this.selectedApplication = application;
    this.newStatus = application.status;
    this.showStatusModal = true;
    this.statusUpdateError = null;
  }

  /**
   * Ferme la modale de changement de statut
   */
  closeStatusModal(): void {
    this.showStatusModal = false;
    this.selectedApplication = null;
    this.newStatus = '';
    this.statusUpdateError = null;
  }

  /**
   * Met à jour le statut de la candidature
   */
  updateStatus(): void {
    if (!this.selectedApplication || !this.newStatus) {
      return;
    }

    this.statusUpdateLoading = true;
    this.statusUpdateError = null;

    this.applicationService.updateApplicationStatus(this.selectedApplication.id, this.newStatus).subscribe({
      next: (updatedApplication) => {
        // Mettre à jour la liste locale
        const index = this.candidates.findIndex(c => c.id === updatedApplication.id);
        if (index !== -1) {
          this.candidates[index] = updatedApplication;
          this.applyFilters();
        }

        this.statusUpdateLoading = false;
        this.closeStatusModal();

        // Afficher un message de succès (optionnel)
        alert(`Candidature mise à jour avec succès!\n\nStatut: ${this.getStatusLabel(this.newStatus)}`);
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du statut:', err);
        this.statusUpdateError = 'Erreur lors de la mise à jour du statut. Veuillez réessayer.';
        this.statusUpdateLoading = false;
      }
    });
  }
}

