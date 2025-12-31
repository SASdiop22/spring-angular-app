import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JobOffer } from 'src/app/models/JobOffer';
import { JobOfferService } from 'src/app/services/job-offer.service';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {

  jobOffers: JobOffer[] = [];

  constructor(private jobOfferService: JobOfferService) { }

  ngOnInit(): void {
    this.jobOfferService.getAllOffers().subscribe({
      next: (data: JobOffer[]) => {
        this.jobOffers = data;
      },
      error: (err: any) => console.error('Error fetching jobs', err)
    });
  }

}
