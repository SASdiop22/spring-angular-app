import { Component, OnInit } from "@angular/core"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { Router, ActivatedRoute } from "@angular/router"
import { JobOfferService } from "../../services/job-offer.service"

@Component({
  selector: "app-add-job-offer",
  templateUrl: "./add-job-offer.component.html",
  styleUrls: ["./add-job-offer.component.scss"],
})
export class AddJobOfferComponent implements OnInit {
  jobOfferForm!: FormGroup
  loading = false
  successMessage = ""
  errorMessage = ""
  contractTypes = ["CDI", "CDD", "Stage", "Alternance", "Freelance"]
  isEditMode = false
  offerId: number | null = null
  closeLoading = false

  constructor(
    private fb: FormBuilder,
    private jobOfferService: JobOfferService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.initializeForm()
    // Vérifier si on est en mode édition
    this.activatedRoute.params.subscribe(params => {
      if (params['id']) {
        this.offerId = Number(params['id'])
        this.isEditMode = true
        this.loadJobOffer(this.offerId)
      }
    })
  }

  initializeForm(): void {
    this.jobOfferForm = this.fb.group({
      title: ["", [Validators.required, Validators.minLength(3)]],
      description: ["", [Validators.required, Validators.minLength(10)]],
      location: ["", [Validators.required]],
      contractType: ["CDI", [Validators.required]],
      salary: ["", [Validators.required, Validators.min(0)]],
      remoteDays: [0, [Validators.required, Validators.min(0), Validators.max(5)]],
      requiredSkills: ["", [Validators.required]],
      companyName: ["", [Validators.required]],
      companyDescription: ["", []],
    })
  }

  loadJobOffer(id: number): void {
    this.loading = true
    this.jobOfferService.getById(id).subscribe({
      next: (offer: any) => {
        // Remplir le formulaire avec les données de l'offre
        this.jobOfferForm.patchValue({
          title: offer.title,
          description: offer.description,
          location: offer.location,
          contractType: offer.contractType,
          salary: offer.salary || "",
          remoteDays: offer.remoteDays || 0,
          requiredSkills: offer.skillsRequired ? offer.skillsRequired.join(", ") : "",
          companyName: offer.companyName || "",
          companyDescription: offer.companyDescription || "",
        })
        this.loading = false
      },
      error: (error) => {
        this.errorMessage = "Erreur lors du chargement de l'offre"
        this.loading = false
        console.error("Error loading job offer:", error)
      },
    })
  }

  submitForm(): void {
    // Marquer tous les champs comme touchés pour afficher les erreurs
    Object.keys(this.jobOfferForm.controls).forEach(key => {
      this.jobOfferForm.get(key)?.markAsTouched();
    });

    if (this.jobOfferForm.invalid) {
      this.errorMessage = "Veuillez remplir tous les champs requis correctement"
      return
    }

    this.loading = true
    this.successMessage = ""
    this.errorMessage = ""

    const formData = this.jobOfferForm.value

    // Convertir les compétences en tableau
    const skills = formData.requiredSkills
      .split(",")
      .map((skill: string) => skill.trim())

    const jobOfferPayload = {
      ...formData,
      requiredSkills: skills,
    }

    if (this.isEditMode && this.offerId) {
      // Mode édition - appeler updateJobOffer
      this.jobOfferService.updateJobOffer(this.offerId, jobOfferPayload).subscribe({
        next: () => {
          this.loading = false
          this.successMessage = "Offre d'emploi mise à jour avec succès!"
          setTimeout(() => {
            this.router.navigate(["/job-offers"])
          }, 1500)
        },
        error: (error) => {
          this.loading = false
          this.errorMessage =
            error.error?.message || "Erreur lors de la mise à jour de l'offre"
        },
      })
    } else {
      // Mode création
      this.jobOfferService.createJobOffer(jobOfferPayload).subscribe({
        next: () => {
          this.loading = false
          this.successMessage = "Offre d'emploi créée avec succès!"
          this.jobOfferForm.reset()
          setTimeout(() => {
            this.router.navigate(["/job-offers"])
          }, 1500)
        },
        error: (error) => {
          this.loading = false
          this.errorMessage =
            error.error?.message || "Erreur lors de la création de l'offre"
        },
      })
    }
  }


  closeOffer(): void {
    if (!this.offerId) return

    if (!confirm("Êtes-vous sûr de vouloir clôturer cette offre d'emploi ? Elle n'apparaîtra plus aux candidats.")) {
      return
    }

    this.closeLoading = true
    this.jobOfferService.closeJobOffer(this.offerId).subscribe({
      next: () => {
        this.closeLoading = false
        this.successMessage = "Offre d'emploi clôturée avec succès!"
        setTimeout(() => {
          this.router.navigate(["/job-offers"])
        }, 1500)
      },
      error: (error) => {
        this.closeLoading = false
        this.errorMessage =
          error.error?.message || "Erreur lors de la clôture de l'offre"
      },
    })
  }

  resetForm(): void {
    this.jobOfferForm.reset()
    this.successMessage = ""
    this.errorMessage = ""
  }

  goBack(): void {
    this.router.navigate(["/job-offers"])
  }
}

