import { Component, OnInit, OnDestroy } from "@angular/core"
import { Router } from "@angular/router"
import type { JobOffer } from "../../models/JobOffer"
import { JobOfferService } from "../../services/job-offer.service"
import { AuthService } from "../../services/auth.service"
import { RoleService } from "../../services/role.service"
import { Subscription } from "rxjs"

@Component({
  selector: "app-job-offers-list",
  templateUrl: "./job-offers-list.component.html",
  styleUrls: ["./job-offers-list.component.scss"],
})
export class JobOffersListComponent implements OnInit, OnDestroy {
  jobOffers: JobOffer[] = []
  filteredOffers: JobOffer[] = []
  loading = true
  error: string | null = null

  // Rôles
  isRH = false
  isCandidat = false
  isVisitor = false
  private roleSubscription: Subscription | null = null

  // Filtres
  searchKeyword = ""
  selectedContractType = ""
  selectedLocation = ""
  minSalary: number | null = null
  maxSalary: number | null = null
  selectedRemoteDays = ""

  // Options pour les filtres
  contractTypes: string[] = []
  locations: string[] = []

  constructor(
    private jobOfferService: JobOfferService,
    private router: Router,
    private authService: AuthService,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    this.updateRoleStatus()
    // S'abonner aux changements de rôle
    this.roleSubscription = this.roleService.role$.subscribe(() => {
      this.updateRoleStatus()
    })
    this.loadJobOffers()
  }

  ngOnDestroy(): void {
    if (this.roleSubscription) {
      this.roleSubscription.unsubscribe()
    }
  }

  updateRoleStatus(): void {
    this.isRH = this.authService.isRH()
    this.isCandidat = this.authService.isCandidat()
    this.isVisitor = !this.authService.authenticated()
  }

  loadJobOffers(): void {
    this.loading = true
    this.jobOfferService.getAllPublished().subscribe({
      next: (offers: JobOffer[]) => {
        this.jobOffers = offers
        this.filteredOffers = offers
        this.extractFilterOptions()
        this.loading = false
      },
      error: (err: any) => {
        this.error = "Erreur lors du chargement des offres"
        this.loading = false
        console.error("[v0] Error loading job offers:", err)
      },
    })
  }

  extractFilterOptions(): void {
    // Extraire les types de contrat uniques
    this.contractTypes = [...new Set(this.jobOffers.map((offer) => offer.contractType || ""))]

    // Extraire les localisations uniques
    this.locations = [...new Set(this.jobOffers.map((offer) => offer.location))]
  }

  applyFilters(): void {
    this.filteredOffers = this.jobOffers.filter((offer) => {
      // Filtre par mot-clé
      const matchesKeyword =
        !this.searchKeyword ||
        offer.title.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
        offer.description.toLowerCase().includes(this.searchKeyword.toLowerCase())

      // Filtre par type de contrat
      const matchesContractType = !this.selectedContractType || offer.contractType === this.selectedContractType

      // Filtre par localisation
      const matchesLocation = !this.selectedLocation || offer.location === this.selectedLocation

      // Filtre par salaire minimum
      const matchesMinSalary = !this.minSalary || (offer.salaryRange && offer.salaryRange >= this.minSalary)

      // Filtre par salaire maximum
      const matchesMaxSalary = !this.maxSalary || (offer.salaryRange && offer.salaryRange <= this.maxSalary)

      // Filtre par jours de télétravail
      const matchesRemoteDays =
        !this.selectedRemoteDays ||
        (offer.remoteDays !== null && offer.remoteDays !== undefined && offer.remoteDays >= Number.parseInt(this.selectedRemoteDays))

      return (
        matchesKeyword &&
        matchesContractType &&
        matchesLocation &&
        matchesMinSalary &&
        matchesMaxSalary &&
        matchesRemoteDays
      )
    })
  }

  resetFilters(): void {
    this.searchKeyword = ""
    this.selectedContractType = ""
    this.selectedLocation = ""
    this.minSalary = null
    this.maxSalary = null
    this.selectedRemoteDays = ""
    this.filteredOffers = this.jobOffers
  }

  searchByKeyword(): void {
    if (this.searchKeyword.trim()) {
      this.loading = true
      this.jobOfferService.search(this.searchKeyword).subscribe({
        next: (offers: JobOffer[]) => {
          this.filteredOffers = offers
          this.loading = false
        },
        error: (err: any) => {
          console.error("[v0] Error searching offers:", err)
          this.loading = false
        },
      })
    } else {
      this.filteredOffers = this.jobOffers
    }
  }

  viewDetails(offerId: number): void {
    this.router.navigate(["/job-offers", offerId])
  }

  editOffer(offerId: number): void {
    this.router.navigate(["/job-offers", offerId, "edit"])
  }
}
