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
  isRH = false
  isAdmin = false
  isCandidat = false
  currentUserId: number | null = null

  // Formulaire de candidature
  cvUrl = ""
  coverLetter = ""
  cvFile: File | null = null
  coverLetterFile: File | null = null
  applying = false
  applicationSuccess = false
  applicationError: string | null = null

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private jobOfferService: JobOfferService,
    private applicationService: ApplicationService,
    public authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.authenticated()
    this.isRH = this.authService.isRH()
    this.isAdmin = this.authService.isAdmin()
    this.isCandidat = this.authService.isCandidat()

    // Récupérer l'ID de l'utilisateur connecté
    this.currentUserId = this.authService.getCurrentUserId()
    console.log('👤 Current User ID:', this.currentUserId)

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
    if (!this.jobOffer || !this.cvFile) {
      this.applicationError = "Veuillez importer votre CV en PDF"
      return
    }

    if (!this.currentUserId) {
      this.applicationError = "Vous devez être connecté pour postuler"
      return
    }

    // Validation du fichier CV
    if (!this.cvFile.name.endsWith('.pdf')) {
      this.applicationError = "Le CV doit être au format PDF"
      return
    }

    if (this.cvFile.size > 5 * 1024 * 1024) {
      this.applicationError = "Le fichier CV ne doit pas dépasser 5 MB"
      return
    }

    // Validation du fichier de lettre de motivation (optionnel)
    if (this.coverLetterFile) {
      if (!this.coverLetterFile.name.endsWith('.pdf')) {
        this.applicationError = "La lettre de motivation doit être au format PDF"
        return
      }
      if (this.coverLetterFile.size > 5 * 1024 * 1024) {
        this.applicationError = "La lettre de motivation ne doit pas dépasser 5 MB"
        return
      }
    }

    // Utiliser l'ID réel du candidat connecté
    const candidateId = this.currentUserId

    this.applying = true
    this.applicationError = null

    // ✅ UTILISER applyWithFiles() pour uploader les fichiers réels
    // coverLetterFile peut être null, donc passer undefined si null
    this.applicationService.applyWithFiles(
      this.jobOffer.id,
      candidateId,
      this.cvFile as File, // cvFile est certainement non-null (vérifié plus haut)
      this.coverLetterFile || undefined // coverLetterFile peut être null
    ).subscribe({
      next: () => {
        this.applicationSuccess = true
        this.applying = false
        this.showApplicationForm = false
        // Réinitialiser le formulaire
        this.cvFile = null
        this.coverLetterFile = null
      },
      error: (err: any) => {
        // Gérer les différents formats d'erreur du backend
        let errorMessage = "Erreur lors de la candidature"

        if (err.error?.error) {
          errorMessage = err.error.error
        } else if (err.error?.message) {
          errorMessage = err.error.message
        } else if (err.status === 409) {
          errorMessage = "Vous avez déjà postulé sur cette offre"
        }

        this.applicationError = errorMessage
        this.applying = false
        console.error("[v0] Error applying:", err)
      },
    })
  }

  onCvFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement
    if (input.files && input.files.length > 0) {
      this.cvFile = input.files[0]
    }
  }

  onCoverLetterFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement
    if (input.files && input.files.length > 0) {
      this.coverLetterFile = input.files[0]
    }
  }

  goBack(): void {
    this.router.navigate(["/job-offers"])
  }
}
