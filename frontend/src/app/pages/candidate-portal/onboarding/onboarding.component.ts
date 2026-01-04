import { Component, OnInit } from '@angular/core';
import { RecruitmentService } from '../../../services/recruitment.service';
import { Application } from '../../../models/Application';

@Component({
    selector: 'app-onboarding',
    templateUrl: './onboarding.component.html',
    styleUrls: ['./onboarding.component.css']
})
export class OnboardingComponent implements OnInit {
    myApplications: Application[] = [];

    constructor(private recruitmentService: RecruitmentService) { }

    ngOnInit(): void {
        // Mock user ID 50 (Jane Doe)
        this.recruitmentService.getMyApplications(50).subscribe(apps => {
            this.myApplications = apps;
        });
    }

    getStepIndex(status: string): number {
        switch (status) {
            case 'RECEIVED': return 0;
            case 'INTERVIEW': return 1;
            case 'ACCEPTED': return 2;
            case 'REJECTED': return 3; // Special case
            default: return 0;
        }
    }
}
