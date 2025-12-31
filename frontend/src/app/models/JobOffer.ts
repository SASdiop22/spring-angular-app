export interface JobOffer {
    id: number;
    title: string;
    description: string;
    location: string;
    salaryRange?: string;
    requirements: string[]; // List of skills/requirements
    postedDate: Date;
    status: 'OPEN' | 'CLOSED' | 'DRAFT';
    tags?: string[]; // For filtering
}
