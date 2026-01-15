import { Component, OnInit } from "@angular/core"
import { Router } from "@angular/router"
import { JobOfferService } from "../../services/job-offer.service"
import { JobOffer } from "../../models/JobOffer"
import { AuthService } from "../../services/auth.service"

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
})
export class HomeComponent implements OnInit {
  jobOffers: JobOffer[] = []
  loading = true
  searchKeyword = ""
  searchLocation = ""
  isRH = false
  isCandidat = false
  userRole = "VISITOR"

  constructor(
    private jobOfferService: JobOfferService,
    private authService: AuthService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.updateUserRole()
    this.loadJobOffers()
  }

  updateUserRole(): void {
    this.userRole = this.authService.getUserRole()
    this.isRH = this.authService.isRH()
    this.isCandidat = this.authService.isCandidat()
  }

  loadJobOffers(): void {
    this.loading = true
    this.jobOfferService.getAllPublished().subscribe({
      next: (offers) => {
        this.jobOffers = offers
        this.loading = false
      },
      error: (err) => {
        console.error("Erreur lors du chargement des offres:", err)
        this.loading = false
      },
    })
  }

  onSearch(): void {
    if (this.searchKeyword.trim()) {
      this.loading = true
      this.jobOfferService.search(this.searchKeyword).subscribe({
        next: (offers) => {
          this.jobOffers = offers
          this.loading = false
        },
        error: (err) => {
          console.error("Erreur lors de la recherche:", err)
          this.loading = false
        },
      })
    } else {
      this.loadJobOffers()
    }
  }

  onViewJobDetail(jobId: number): void {
    this.router.navigate(["/job-offers", jobId])
  }

  onApply(jobId: number): void {
    if (this.authService.authenticated()) {
      this.router.navigate(["/job-offers", jobId])
    } else {
      this.router.navigate(["/login"], { queryParams: { returnUrl: `/job-offers/${jobId}` } })
    }
  }

  goToJobOffers(): void {
    this.router.navigate(["/job-offers"])
  }

  isAuthenticated(): boolean {
    return this.authService.authenticated()
  }

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/"])
  }

  getTimeSincePublished(publishedAt: string | undefined): string {
    if (!publishedAt) return "Récemment"

    const published = new Date(publishedAt)
    const now = new Date()
    const diffTime = Math.abs(now.getTime() - published.getTime())
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

    if (diffDays === 0) return "Aujourd'hui"
    if (diffDays === 1) return "Il y a 1 jour"
    if (diffDays < 7) return `Il y a ${diffDays} jours`
    if (diffDays < 30) return `Il y a ${Math.floor(diffDays / 7)} semaines`
    return `Il y a ${Math.floor(diffDays / 30)} mois`
  }
}

