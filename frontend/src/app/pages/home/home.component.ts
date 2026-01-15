import { Component,  OnInit } from "@angular/core"
import  { Router } from "@angular/router"
import  { JobOfferService } from "../../services/job-offer.service"
import  { JobOffer } from "../../models/JobOffer"
import  { AuthService } from "../../services/auth.service"

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

  constructor(
    private jobOfferService: JobOfferService,
    private authService: AuthService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.loadJobOffers()
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
      // Rediriger vers la page de candidature
      this.router.navigate(["/job-offers", jobId, "apply"])
    } else {
      // Rediriger vers la page de connexion
      this.router.navigate(["/login"], { queryParams: { returnUrl: `/job-offers/${jobId}` } })
    }
  }

  goToLogin(): void {
    this.router.navigate(["/login"])
  }

  goToRegister(): void {
    this.router.navigate(["/register"])
  }

  goToJobOffers(): void {
    this.router.navigate(["/job-offers"])
  }

  isAuthenticated(): boolean {
    return this.authService.authenticated()
  }

  logout(): void {
    sessionStorage.removeItem("ACCESS_TOKEN")
    this.router.navigate(["/login"])
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
