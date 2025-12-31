import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { JobOffer } from '../models/JobOffer';

@Injectable({
    providedIn: 'root'
})
export class JobOfferService {

    private mockOffers: JobOffer[] = [
        {
            id: 1,
            title: 'Senior Frontend Developer',
            description: 'We are looking for an experienced Angular developer to lead our frontend team. You will be responsible for architecture and code quality.',
            location: 'Paris, France (Remote)',
            salaryRange: '50k - 70k €',
            requirements: ['Angular 17+', 'TypeScript', 'Tailwind CSS', 'RxJS'],
            postedDate: new Date('2023-10-01'),
            status: 'OPEN',
            tags: ['Frontend', 'Angular', 'Senior']
        },
        {
            id: 2,
            title: 'Backend Engineer (Spring Boot)',
            description: 'Join our backend team to build robust APIs using Spring Boot. You will work on microservices and high-performance systems.',
            location: 'Lyon, France',
            salaryRange: '45k - 60k €',
            requirements: ['Java 17', 'Spring Boot', 'PostgreSQL', 'Docker'],
            postedDate: new Date('2023-10-05'),
            status: 'OPEN',
            tags: ['Backend', 'Java', 'Spring']
        },
        {
            id: 3,
            title: 'UI/UX Designer',
            description: 'Create beautiful user experiences for our recruitment platform. Work closely with product managers and developers.',
            location: 'Bordeaux, France',
            salaryRange: '40k - 55k €',
            requirements: ['Figma', 'Adobe XD', 'User Research', 'Prototyping'],
            postedDate: new Date('2023-10-10'),
            status: 'OPEN',
            tags: ['Design', 'UI/UX']
        }
    ];

    constructor() { }

    getAllOffers(): Observable<JobOffer[]> {
        return of(this.mockOffers);
    }

    getOfferById(id: number): Observable<JobOffer | undefined> {
        const offer = this.mockOffers.find(o => o.id === id);
        return of(offer);
    }

    createOffer(offer: JobOffer): Observable<JobOffer> {
        offer.id = this.mockOffers.length + 1;
        this.mockOffers.push(offer);
        return of(offer);
    }
}
