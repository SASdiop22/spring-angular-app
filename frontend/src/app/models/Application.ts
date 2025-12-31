import { JobOffer } from './JobOffer';

export interface Application {
    id: number;
    candidateName: string;
    candidateEmail: string;
    jobOfferId: number;
    jobOfferTitle?: string; // For display convenience
    status: 'RECEIVED' | 'INTERVIEW' | 'REJECTED' | 'ACCEPTED';
    submissionDate: Date;
    resumeUrl?: string; // URL to the file
    coverLetterUrl?: string;
}
