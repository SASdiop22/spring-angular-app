import { Component, OnInit, OnDestroy } from "@angular/core"
import { Router } from "@angular/router"
import { AuthService } from "../../services/auth.service"
import { RoleService } from "../../services/role.service"
import { Subscription } from "rxjs"

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated = false
  isRH = false
  isCandidat = false
  userRole = "VISITOR"
  showMenu = false
  private roleSubscription: Subscription | null = null

  constructor(
    public router: Router,
    private authService: AuthService,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    this.updateAuthStatus()
    // S'abonner aux changements de rôle
    this.roleSubscription = this.roleService.role$.subscribe((role) => {
      this.userRole = role
      this.isRH = this.roleService.isRH()
      this.isCandidat = this.roleService.isCandidat()
      this.isAuthenticated = this.authService.authenticated()
    })
  }

  ngOnDestroy(): void {
    if (this.roleSubscription) {
      this.roleSubscription.unsubscribe()
    }
  }

  updateAuthStatus(): void {
    this.isAuthenticated = this.authService.authenticated()
    this.userRole = this.authService.getUserRole()
    this.isRH = this.authService.isRH()
    this.isCandidat = this.authService.isCandidat()
  }

  toggleMenu(): void {
    this.showMenu = !this.showMenu
  }

  logout(): void {
    this.authService.logout()
    this.updateAuthStatus()
    this.router.navigate(["/"])
  }

  goHome(): void {
    this.router.navigate(["/"])
  }

  goToJobOffers(): void {
    this.router.navigate(["/job-offers"])
  }

  goToCandidates(): void {
    this.router.navigate(["/candidates"])
  }

  goToDashboard(): void {
    this.router.navigate(["/dashboard"])
  }

  goToProfile(): void {
    this.router.navigate(["/user/profile"])
  }

  goToAddJobOffer(): void {
    this.router.navigate(["/add-job-offer"])
  }
}

