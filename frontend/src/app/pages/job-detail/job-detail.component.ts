import { Component, OnInit } from "@angular/core"
import { ActivatedRoute, Router } from "@angular/router"
import type { JobOffer } from "../../models/JobOffer"
import { JobOfferService } from "../../services/job-offer.service"
import { ApplicationService } from "../../services/application.service"
import { AuthService } from "../../services/auth.service"

@Component({
  selector: "app-job-detail",
  templateUrl: "./job-detail.component.html",
  styleUrls: ["./job-detail.component.scss"],
})
export class JobDetailComponent implements OnInit {
  jobOffer: JobOffer | null = null
  loading = true
  error: string | null = null

  showApplicationForm = false
  isAuthenticated = false

  // Formulaire de candidature
  cvUrl = ""
  coverLetter = ""
  applying = false
  applicationSuccess = false
  applicationError: string | null = null

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private jobOfferService: JobOfferService,
    private applicationService: ApplicationService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.authenticated()
    const id = this.route.snapshot.paramMap.get("id")

    if (id) {
      this.loadJobOffer(Number.parseInt(id))
    }
  }

  loadJobOffer(id: number): void {
    this.loading = true
    this.jobOfferService.getById(id).subscribe({
      next: (offer: JobOffer) => {
        this.jobOffer = offer
        this.loading = false
      },
      error: (err: any) => {
        this.error = "Erreur lors du chargement de l'offre"
        this.loading = false
        console.error("[v0] Error loading job offer:", err)
      },
    })
  }

  toggleApplicationForm(): void {
    if (!this.isAuthenticated) {
      this.router.navigate(["/login"], { queryParams: { returnUrl: this.router.url } })
      return
    }
    this.showApplicationForm = !this.showApplicationForm
  }

  submitApplication(): void {
    if (!this.jobOffer || !this.cvUrl.trim()) {
      this.applicationError = "Veuillez remplir tous les champs obligatoires"
      return
    }

    // TODO: Récupérer le vrai candidateId depuis l'utilisateur connecté
    const candidateId = 1 // À remplacer par l'ID réel du candidat connecté

    this.applying = true
    this.applicationError = null

    this.applicationService.apply(this.jobOffer.id, candidateId, this.cvUrl, this.coverLetter).subscribe({
      next: () => {
        this.applicationSuccess = true
        this.applying = false
        this.showApplicationForm = false
        // Réinitialiser le formulaire
        this.cvUrl = ""
        this.coverLetter = ""
      },
      error: (err: any) => {
        this.applicationError = err.error?.message || "Erreur lors de la candidature"
        this.applying = false
        console.error("[v0] Error applying:", err)
      },
    })
  }

  goBack(): void {
    this.router.navigate(["/job-offers"])
  }
}
