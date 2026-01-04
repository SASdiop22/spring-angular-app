import { Component, OnInit } from '@angular/core';
import { JobOffer } from '../../../models/JobOffer';
import { RecruitmentService } from '../../../services/recruitment.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'app-job-management',
    templateUrl: './job-management.component.html',
    styleUrls: ['./job-management.component.css']
})
export class JobManagementComponent implements OnInit {
    jobOffers: JobOffer[] = [];
    jobForm: FormGroup;
    isEditing = false;

    constructor(private recruitmentService: RecruitmentService, private fb: FormBuilder) {
        this.jobForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            requirements: [''],
            location: ['', Validators.required],
            status: ['OPEN']
        });
    }

    ngOnInit(): void {
        this.loadJobOffers();
    }

    loadJobOffers() {
        this.recruitmentService.getJobOffers().subscribe(offers => {
            this.jobOffers = offers;
        });
    }

    onSubmit() {
        if (this.jobForm.valid) {
            const newOffer: JobOffer = this.jobForm.value;
            this.recruitmentService.createJobOffer(newOffer).subscribe(offer => {
                this.loadJobOffers();
                this.jobForm.reset({ status: 'OPEN' });
            });
        }
    }
}
