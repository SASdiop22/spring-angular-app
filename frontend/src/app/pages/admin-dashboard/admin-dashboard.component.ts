import { Component, OnInit } from "@angular/core"
import { AdminService } from "../../services/admin.service"
import { AuthService } from "../../services/auth.service"
import { Router } from "@angular/router"

@Component({
  selector: "app-admin-dashboard",
  templateUrl: "./admin-dashboard.component.html",
  styleUrls: ["./admin-dashboard.component.scss"],
})
export class AdminDashboardComponent implements OnInit {
  users: any[] = []
  jobOffers: any[] = []
  loading = false
  activeTab: "users" | "joboffers" = "users"
  error: string | null = null

  selectedUserForDelete: any = null
  selectedOfferForDelete: any = null
  showConfirmDeleteUser = false
  showConfirmDeleteOffer = false

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Vérifier que c'est un admin
    if (!this.authService.isAdmin()) {
      this.router.navigate(["/"])
      return
    }

    this.loadUsers()
  }

  /**
   * Charge tous les utilisateurs
   */
  loadUsers(): void {
    this.loading = true
    this.error = null

    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur lors du chargement des utilisateurs:", err)
        this.error = "Erreur lors du chargement des utilisateurs"
        this.loading = false
      },
    })
  }

  /**
   * Charge toutes les offres d'emploi
   */
  loadJobOffers(): void {
    this.loading = true
    this.error = null

    this.adminService.getAllJobOffers().subscribe({
      next: (offers) => {
        this.jobOffers = offers
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur lors du chargement des offres:", err)
        this.error = "Erreur lors du chargement des offres d'emploi"
        this.loading = false
      },
    })
  }

  /**
   * Change l'onglet actif
   */
  switchTab(tab: "users" | "joboffers"): void {
    this.activeTab = tab
    if (tab === "users") {
      this.loadUsers()
    } else {
      this.loadJobOffers()
    }
  }

  /**
   * Ouvre la confirmation de suppression d'utilisateur
   */
  confirmDeleteUser(user: any): void {
    this.selectedUserForDelete = user
    this.showConfirmDeleteUser = true
  }

  /**
   * Annule la suppression d'utilisateur
   */
  cancelDeleteUser(): void {
    this.showConfirmDeleteUser = false
    this.selectedUserForDelete = null
  }

  /**
   * Supprime définitivement un utilisateur
   */
  deleteUserConfirmed(): void {
    if (!this.selectedUserForDelete) return

    const userId = this.selectedUserForDelete.id
    this.loading = true

    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        console.log("✅ Utilisateur supprimé avec succès")
        this.users = this.users.filter((u) => u.id !== userId)
        this.showConfirmDeleteUser = false
        this.selectedUserForDelete = null
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur lors de la suppression:", err)
        this.error = "Erreur lors de la suppression de l'utilisateur"
        this.loading = false
      },
    })
  }

  /**
   * Ouvre la confirmation de suppression d'offre
   */
  confirmDeleteOffer(offer: any): void {
    this.selectedOfferForDelete = offer
    this.showConfirmDeleteOffer = true
  }

  /**
   * Annule la suppression d'offre
   */
  cancelDeleteOffer(): void {
    this.showConfirmDeleteOffer = false
    this.selectedOfferForDelete = null
  }

  /**
   * Supprime définitivement une offre d'emploi
   */
  deleteOfferConfirmed(): void {
    if (!this.selectedOfferForDelete) return

    const offerId = this.selectedOfferForDelete.id
    this.loading = true

    this.adminService.deleteJobOffer(offerId).subscribe({
      next: () => {
        console.log("✅ Offre supprimée avec succès")
        this.jobOffers = this.jobOffers.filter((o) => o.id !== offerId)
        this.showConfirmDeleteOffer = false
        this.selectedOfferForDelete = null
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur lors de la suppression:", err)
        this.error = "Erreur lors de la suppression de l'offre"
        this.loading = false
      },
    })
  }

  /**
   * Archive une offre (sans la supprimer)
   */
  archiveOffer(offer: any): void {
    this.loading = true

    this.adminService.archiveJobOffer(offer.id).subscribe({
      next: () => {
        console.log("✅ Offre archivée")
        offer.status = "CLOSED"
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur:", err)
        this.error = "Erreur lors de l'archivage"
        this.loading = false
      },
    })
  }

  /**
   * Retourner à l'accueil
   */
  goBack(): void {
    this.router.navigate(["/"])
  }

  /**
   * Retourne le label du rôle en français
   */
  getRoleLabel(roles: string[]): string {
    if (!roles || roles.length === 0) return "Aucun rôle"
    const roleMap: { [key: string]: string } = {
      CANDIDAT: "Candidat",
      EMPLOYE: "Employé",
      RH: "RH",
      ADMIN: "Admin",
    }
    return roles.map((r) => roleMap[r] || r).join(", ")
  }

  /**
   * Retourne le label du statut en français
   */
  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      DRAFT: "Brouillon",
      PENDING: "En attente",
      OPEN: "Ouverte",
      CLOSED: "Fermée",
      FILLED: "Pourvue",
    }
    return statusMap[status] || status
  }

  /**
   * Retourne la classe Bootstrap pour la couleur du statut
   */
  getOfferStatusClass(status: string): string {
    switch (status) {
      case "DRAFT":
        return "secondary"
      case "PENDING":
        return "warning"
      case "OPEN":
        return "success"
      case "CLOSED":
        return "danger"
      case "FILLED":
        return "primary"
      default:
        return "dark"
    }
  }
}

