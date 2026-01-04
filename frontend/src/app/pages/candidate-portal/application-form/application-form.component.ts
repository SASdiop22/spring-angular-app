import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RecruitmentService } from '../../../services/recruitment.service';
import { JobOffer } from '../../../models/JobOffer';

@Component({
    selector: 'app-application-form',
    templateUrl: './application-form.component.html',
    styleUrls: ['./application-form.component.css']
})
export class ApplicationFormComponent implements OnInit {
    jobId: number | null = null;
    job: JobOffer | undefined;
    applyForm: FormGroup;
    isSubmitting = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private fb: FormBuilder,
        private recruitmentService: RecruitmentService
    ) {
        this.applyForm = this.fb.group({
            firstName: ['', Validators.required],
            lastName: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            resume: [null, Validators.required],
            coverLetter: [null]
        });
    }

    ngOnInit(): void {
        this.jobId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.jobId) {
            this.recruitmentService.getJobOfferById(this.jobId).subscribe(offer => {
                this.job = offer;
            });
        }
    }

    onSubmit() {
        if (this.applyForm.valid && this.job) {
            this.isSubmitting = true;
            // Mock submission
            setTimeout(() => {
                alert('Application submitted successfully!');
                this.router.navigate(['/onboarding']);
            }, 1500);
        }
    }

    onFileChange(event: any, field: string) {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            this.applyForm.patchValue({
                [field]: file
            });
        }
    }
}
