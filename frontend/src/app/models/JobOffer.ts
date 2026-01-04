export interface JobOffer {
    id: number;
    title: string;
    description: string;
    requirements: string;
    location: string;
    status: 'OPEN' | 'CLOSED' | 'DRAFT';
    postedDate: Date;
}
