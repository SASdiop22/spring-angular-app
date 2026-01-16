import { Component, OnInit } from "@angular/core"
import { Router } from "@angular/router"
import { AuthService } from "../../services/auth.service"

@Component({
  selector: "app-candidates",
  templateUrl: "./candidates.component.html",
  styleUrls: ["./candidates.component.scss"],
})
export class CandidatesComponent implements OnInit {
  candidates = [
    {
      id: 1,
      name: "Jean Dupont",
      title: "Développeur Full Stack",
      skills: ["Angular", "TypeScript", "Spring Boot", "MySQL"],
      location: "Paris",
    },
    {
      id: 2,
      name: "Marie Martin",
      title: "Développeuse Frontend",
      skills: ["React", "TypeScript", "CSS3", "JavaScript"],
      location: "Lyon",
    },
    {
      id: 3,
      name: "Pierre Bernard",
      title: "Architecte Logiciel",
      skills: ["Java", "Spring Cloud", "Kubernetes", "AWS"],
      location: "Toulouse",
    },
    {
      id: 4,
      name: "Sophie Leclerc",
      title: "Data Scientist",
      skills: ["Python", "Machine Learning", "Pandas", "TensorFlow"],
      location: "Bordeaux",
    },
  ]

  isRH = false
  loading = false

  constructor(
    private authService: AuthService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.isRH = this.authService.isRH()
  }

  viewCandidateProfile(candidateId: number): void {
    this.router.navigate(["/candidate", candidateId])
  }

  contactCandidate(candidateId: number): void {
    // Implémentation de la logique de contact
    alert(`Contact envoyé au candidat ${candidateId}`)
  }

  goBack(): void {
    this.router.navigate(["/"])
  }
}

