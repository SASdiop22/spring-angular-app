import { Component, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { ApplicationService } from '../../services/application.service'
import { AuthService } from '../../services/auth.service'
import type { Application } from '../../models/Application'

@Component({
  selector: 'app-user-applications',
  templateUrl: './user-applications.component.html',
  styleUrls: ['./user-applications.component.scss']
})
export class UserApplicationsComponent implements OnInit {
  applications: Application[] = []
  filteredApplications: Application[] = []
  loading = true
  error: string | null = null

  // Filtres
  statusFilter = 'ALL'
  searchQuery = ''
  sortBy = 'recent'

  // Pagination
  currentPage = 0
  pageSize = 10

  constructor(
    private applicationService: ApplicationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadApplications()
  }

  /**
   * Charge les candidatures de l'utilisateur
   */
  loadApplications(): void {
    this.loading = true
    this.error = null

    const userId = this.authService.getCurrentUserId()
    if (!userId) {
      this.error = "Impossible de charger vos candidatures"
      this.loading = false
      return
    }

    this.applicationService.getApplicationsByCandidate(userId).subscribe({
      next: (apps: Application[]) => {
        this.applications = apps
        this.applyFilters()
        this.loading = false
      },
      error: (err) => {
        console.error('Erreur lors du chargement des candidatures:', err)
        this.error = "Erreur lors du chargement de vos candidatures"
        this.loading = false
      }
    })
  }

  /**
   * Applique les filtres et le tri
   */
  applyFilters(): void {
    let filtered = [...this.applications]

    // Filtrer par statut
    if (this.statusFilter !== 'ALL') {
      filtered = filtered.filter(app => app.status === this.statusFilter)
    }

    // Filtrer par recherche
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase()
      filtered = filtered.filter(app => {
        const jobTitle = app.jobOfferTitle || ''
        return jobTitle.toLowerCase().includes(query)
      })
    }

    // Trier
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'recent':
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        case 'oldest':
          return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
        case 'status':
          return (a.status || '').localeCompare(b.status || '')
        default:
          return 0
      }
    })

    this.filteredApplications = filtered
    this.currentPage = 0
  }

  /**
   * Retourne les candidatures de la page actuelle
   */
  getPaginatedApplications(): Application[] {
    const start = this.currentPage * this.pageSize
    const end = start + this.pageSize
    return this.filteredApplications.slice(start, end)
  }

  /**
   * Retourne le nombre total de pages
   */
  getTotalPages(): number {
    return Math.ceil(this.filteredApplications.length / this.pageSize)
  }

  /**
   * Obtient la couleur du badge de statut
   */
  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACCEPTED':
        return 'status-accepted'
      case 'REJECTED':
        return 'status-rejected'
      case 'PENDING':
        return 'status-pending'
      case 'INTERVIEWING':
        return 'status-interviewing'
      default:
        return 'status-default'
    }
  }

  /**
   * Obtient le label du statut
   */
  getStatusLabel(status: string): string {
    switch (status) {
      case 'ACCEPTED':
        return 'Accepté'
      case 'REJECTED':
        return 'Rejeté'
      case 'PENDING':
        return 'En attente'
      case 'INTERVIEWING':
        return 'En entretien'
      default:
        return status
    }
  }

  /**
   * Va voir les détails d'une candidature
   */
  viewApplicationDetails(applicationId: number): void {
    this.router.navigate(['/applications', applicationId])
  }

  /**
   * Revient à l'accueil
   */
  goBack(): void {
    this.router.navigate(['/'])
  }

  /**
   * Traite les changements de filtre
   */
  onStatusFilterChange(): void {
    this.applyFilters()
  }

  onSearchChange(): void {
    this.applyFilters()
  }

  onSortChange(): void {
    this.applyFilters()
  }

  /**
   * Changement de page
   */
  nextPage(): void {
    if (this.currentPage < this.getTotalPages() - 1) {
      this.currentPage++
    }
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--
    }
  }
}

