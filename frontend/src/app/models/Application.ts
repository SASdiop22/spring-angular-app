import { User } from './User';
import { JobOffer } from './JobOffer';

export interface Application {
    id: number;
    applicant: User;
    jobOffer: JobOffer;
    status: 'RECEIVED' | 'INTERVIEW' | 'ACCEPTED' | 'REJECTED';
    submissionDate: Date;
    resumeUrl?: string; // Mock URL for now
    coverLetterUrl?: string; // Mock URL for now
    hrComment?: string;
}
