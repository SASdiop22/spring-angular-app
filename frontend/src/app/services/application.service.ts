import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Application } from '../models/Application';

@Injectable({
    providedIn: 'root'
})
export class ApplicationService {

    private mockApplications: Application[] = [];

    constructor() { }

    submitApplication(application: Application): Observable<Application> {
        application.id = this.mockApplications.length + 1;
        application.submissionDate = new Date();
        application.status = 'RECEIVED';
        this.mockApplications.push(application);
        console.log('Application submitted:', application);
        return of(application);
    }

    getApplicationsByOffer(offerId: number): Observable<Application[]> {
        return of(this.mockApplications.filter(a => a.jobOfferId === offerId));
    }
}
