import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { JobOffer } from '../models/JobOffer';
import { Application } from '../models/Application';
import { User } from '../models/User';

@Injectable({
    providedIn: 'root'
})
export class RecruitmentService {

    // MOCK DATA
    private mockOffers: JobOffer[] = [
        {
            id: 1,
            title: 'Senior Java Developer',
            description: 'We are looking for an experienced Java developer to join our backend team.',
            requirements: 'Java 17, Spring Boot, 5+ years experience',
            location: 'Paris, France',
            status: 'OPEN',
            postedDate: new Date('2023-10-01')
        },
        {
            id: 2,
            title: 'Frontend Angular Engineer',
            description: 'Join our frontend team to build modern web applications.',
            requirements: 'Angular 16+, TypeScript, CSS/SCSS',
            location: 'Remote',
            status: 'OPEN',
            postedDate: new Date('2023-10-05')
        },
        {
            id: 3,
            title: 'Product Manager',
            description: 'Lead the product vision for our new recruitment platform.',
            requirements: 'Agile, Scrum, Product Lifecycle Management',
            location: 'Lyon, France',
            status: 'DRAFT',
            postedDate: new Date('2023-10-10')
        }
    ];

    private mockApplications: Application[] = [
        {
            id: 101,
            applicant: { id: 50, username: 'jane.doe', email: 'jane@example.com', role: 'CANDIDATE', firstName: 'Jane', lastName: 'Doe' },
            jobOffer: this.mockOffers[0],
            status: 'RECEIVED',
            submissionDate: new Date('2023-10-12'),
            resumeUrl: 'assets/mock-cv.pdf'
        },
        {
            id: 102,
            applicant: { id: 51, username: 'john.smith', email: 'john@example.com', role: 'CANDIDATE', firstName: 'John', lastName: 'Smith' },
            jobOffer: this.mockOffers[0],
            status: 'INTERVIEW',
            submissionDate: new Date('2023-10-15'),
            resumeUrl: 'assets/mock-cv-2.pdf'
        }
    ];

    constructor() { }

    // --- JOB OFFERS ---

    getJobOffers(): Observable<JobOffer[]> {
        // Return all offers (simulate network delay if needed, but 'of' is fine)
        return of(this.mockOffers);
    }

    getJobOfferById(id: number): Observable<JobOffer | undefined> {
        const offer = this.mockOffers.find(o => o.id === id);
        return of(offer);
    }

    createJobOffer(offer: JobOffer): Observable<JobOffer> {
        offer.id = Math.floor(Math.random() * 1000) + 100; // Generate rough ID
        offer.postedDate = new Date();
        this.mockOffers.push(offer);
        return of(offer);
    }

    updateJobOffer(offer: JobOffer): Observable<JobOffer> {
        const index = this.mockOffers.findIndex(o => o.id === offer.id);
        if (index !== -1) {
            this.mockOffers[index] = offer;
        }
        return of(offer);
    }

    // --- APPLICATIONS ---

    getApplicationsForOffer(offerId: number): Observable<Application[]> {
        const apps = this.mockApplications.filter(a => a.jobOffer.id === offerId);
        return of(apps);
    }

    getMyApplications(userId: number): Observable<Application[]> {
        const apps = this.mockApplications.filter(a => a.applicant.id === userId);
        return of(apps);
    }

    submitApplication(application: Application): Observable<Application> {
        application.id = Math.floor(Math.random() * 10000);
        application.submissionDate = new Date();
        application.status = 'RECEIVED';
        this.mockApplications.push(application);
        return of(application);
    }

    updateApplicationStatus(applicationId: number, status: 'RECEIVED' | 'INTERVIEW' | 'ACCEPTED' | 'REJECTED', comment?: string): Observable<Application> {
        const app = this.mockApplications.find(a => a.id === applicationId);
        if (app) {
            app.status = status;
            if (comment) app.hrComment = comment;
        }
        // Return copy to avoid mutation reference issues in subscribers
        return of(app ? { ...app } : null as any);
    }
}
