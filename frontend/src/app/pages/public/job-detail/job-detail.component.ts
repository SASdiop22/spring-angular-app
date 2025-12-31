import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common'; // Fix: Import Location
import { JobOffer } from 'src/app/models/JobOffer';
import { JobOfferService } from 'src/app/services/job-offer.service';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './job-detail.component.html',
  styleUrls: ['./job-detail.component.scss']
})
export class JobDetailComponent implements OnInit {

  offer: JobOffer | undefined;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private jobOfferService: JobOfferService,
    private location: Location
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.jobOfferService.getOfferById(id).subscribe({
        next: (data: JobOffer | undefined) => {
          this.offer = data;
          this.isLoading = false;
        },
        error: (err: any) => {
          console.error(err);
          this.isLoading = false;
        }
      });
    }
  }

  goBack(): void {
    this.location.back();
  }

}
