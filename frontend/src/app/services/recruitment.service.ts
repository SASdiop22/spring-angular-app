import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JobOffer } from '../models/JobOffer';
import { Application } from '../models/Application';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class RecruitmentService {
    private apiUrl = environment.apiBaseUrl; // e.g., 'http://localhost:8080/api'

    constructor(private http: HttpClient) { }

    // --- JOB OFFERS ---
    getJobOffers(): Observable<JobOffer[]> {
        return this.http.get<JobOffer[]>(`${this.apiUrl}/offers`);
    }

    getJobOfferById(id: number): Observable<JobOffer> {
        return this.http.get<JobOffer>(`${this.apiUrl}/offers/${id}`);
    }

    createJobOffer(offer: JobOffer): Observable<JobOffer> {
        return this.http.post<JobOffer>(`${this.apiUrl}/offers`, offer);
    }

    updateJobOffer(offer: JobOffer): Observable<JobOffer> {
        return this.http.put<JobOffer>(`${this.apiUrl}/offers/${offer.id}`, offer);
    }

    // --- APPLICATIONS ---
    getApplicationsForOffer(offerId: number): Observable<Application[]> {
        return this.http.get<Application[]>(`${this.apiUrl}/offers/${offerId}/applications`);
    }

    getMyApplications(userId: number): Observable<Application[]> {
        return this.http.get<Application[]>(`${this.apiUrl}/users/${userId}/applications`);
    }

    submitApplication(application: Application): Observable<Application> {
        return this.http.post<Application>(`${this.apiUrl}/applications`, application);
    }

    updateApplicationStatus(applicationId: number, status: 'RECEIVED' | 'INTERVIEW' | 'ACCEPTED' | 'REJECTED', comment?: string): Observable<Application> {
        const body: any = { status };
        if (comment) body.comment = comment;
        return this.http.patch<Application>(`${this.apiUrl}/applications/${applicationId}`, body);
    }
}
