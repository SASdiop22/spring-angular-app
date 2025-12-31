import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Important
import { RouterModule } from '@angular/router'; // For routerLink
import { JobOffer } from '../../../models/JobOffer';
import { JobOfferService } from '../../../services/job-offer.service';

@Component({
  selector: 'app-offer-management',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './offer-management.component.html',
  styleUrls: ['./offer-management.component.scss']
})
export class OfferManagementComponent implements OnInit {

  offers: JobOffer[] = [];
  displayedColumns: string[] = ['title', 'location', 'postedDate', 'status', 'actions'];

  constructor(private jobOfferService: JobOfferService) { }

  ngOnInit(): void {
    this.loadOffers();
  }

  loadOffers(): void {
    this.jobOfferService.getAllOffers().subscribe(data => {
      this.offers = data;
    });
  }

  deleteOffer(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette offre ?')) {
      // Mock delete
      this.offers = this.offers.filter(o => o.id !== id);
    }
  }

}
