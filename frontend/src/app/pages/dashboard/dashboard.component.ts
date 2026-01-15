import { Component, OnInit } from "@angular/core"
import { Router } from "@angular/router"
import { AuthService } from "../../services/auth.service"

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit {
  userRole = "VISITOR"
  isRH = false
  isCandidat = false
  userName = ""

  // Stats
  applicationCount = 0
  savedJobsCount = 0
  jobOffersCount = 0
  candidatesCount = 0

  // Recent activities
  recentActivities: any[] = []

  constructor(
    private authService: AuthService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.loadUserInfo()
    this.loadStats()
  }

  loadUserInfo(): void {
    this.userRole = this.authService.getUserRole()
    this.isRH = this.authService.isRH()
    this.isCandidat = this.authService.isCandidat()
  }

  loadStats(): void {
    // Les stats seront chargées depuis l'API
    if (this.isCandidat) {
      this.applicationCount = 5
      this.savedJobsCount = 12
      this.recentActivities = [
        {
          title: "Candidature envoyée",
          description: "Développeur Full Stack - TechCorp",
          date: "Il y a 2 jours",
        },
        {
          title: "Profil mis à jour",
          description: "Compétences ajoutées: Kubernetes",
          date: "Il y a 5 jours",
        },
      ]
    } else if (this.isRH) {
      this.jobOffersCount = 8
      this.candidatesCount = 145
      this.recentActivities = [
        {
          title: "Nouvelle candidature",
          description: "Développeur Frontend - Offre Senior",
          date: "Il y a 1 jour",
        },
        {
          title: "Offre d'emploi publiée",
          description: "Architecte Logiciel - 5 ans d'expérience",
          date: "Il y a 3 jours",
        },
      ]
    }
  }

  goToJobOffers(): void {
    this.router.navigate(["/job-offers"])
  }

  goToCandidates(): void {
    this.router.navigate(["/candidates"])
  }

  goToAddJobOffer(): void {
    this.router.navigate(["/add-job-offer"])
  }

  goToProfile(): void {
    this.router.navigate(["/profile"])
  }
}

