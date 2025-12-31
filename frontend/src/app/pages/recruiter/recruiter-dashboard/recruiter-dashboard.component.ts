import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JobOfferService } from '../../../services/job-offer.service';

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './recruiter-dashboard.component.html',
  styleUrls: ['./recruiter-dashboard.component.scss']
})
export class RecruiterDashboardComponent implements OnInit {

  totalOffers = 0;
  totalCandidates = 12; // Mock data for now
  activeOffers = 0;

  constructor(private jobOfferService: JobOfferService) { }

  ngOnInit(): void {
    this.jobOfferService.getAllOffers().subscribe(offers => {
      this.totalOffers = offers.length;
      this.activeOffers = offers.filter(o => o.status === 'OPEN').length;
    });
  }

}
