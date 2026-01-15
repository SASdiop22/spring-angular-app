import { Component, OnInit } from "@angular/core"
import { Router } from "@angular/router"
import { AuthService } from "../../services/auth.service"

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  isAuthenticated = false
  showMenu = false

  constructor(
    public router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.authenticated()
  }

  toggleMenu(): void {
    this.showMenu = !this.showMenu
  }

  logout(): void {
    sessionStorage.removeItem("ACCESS_TOKEN")
    this.router.navigate(["/login"])
  }

  goHome(): void {
    this.router.navigate(["/"])
  }

  goToJobOffers(): void {
    this.router.navigate(["/job-offers"])
  }

  goToProfile(): void {
    this.router.navigate(["/profile"])
  }
}

