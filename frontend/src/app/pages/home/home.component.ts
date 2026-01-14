import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { JobOfferService } from '../../services/job-offer.service';
import { JobOffer } from '../../models/job-offer.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featuredJobs: JobOffer[] = [];
  isLoading = true;

  constructor(
    private router: Router,
    private authService: AuthService,
    private jobOfferService: JobOfferService
  ) {}

  ngOnInit(): void {
    this.loadFeaturedJobs();
  }

  loadFeaturedJobs(): void {
    this.jobOfferService.getAllPublished().subscribe({
      next: (jobs) => {
        this.featuredJobs = jobs.slice(0, 3);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  navigateToJobs(): void {
    this.router.navigate(['/jobs']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}
