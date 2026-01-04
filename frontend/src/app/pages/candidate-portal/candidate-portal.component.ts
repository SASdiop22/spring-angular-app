import { Component, OnInit } from '@angular/core';
import { JobOffer } from '../../models/JobOffer';
import { RecruitmentService } from '../../services/recruitment.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-candidate-portal',
    templateUrl: './candidate-portal.component.html',
    styleUrls: ['./candidate-portal.component.css']
})
export class CandidatePortalComponent implements OnInit {
    activeJobs: JobOffer[] = [];

    constructor(private recruitmentService: RecruitmentService, private router: Router) { }

    ngOnInit(): void {
        this.recruitmentService.getJobOffers().subscribe(offers => {
            this.activeJobs = offers.filter(o => o.status === 'OPEN');
        });
    }

    apply(jobId: number) {
        this.router.navigate(['/jobs', jobId, 'apply']);
    }
}
