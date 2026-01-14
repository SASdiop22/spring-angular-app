export interface JobOffer {
  id: number;
  title: string;
  description: string;
  deadline: string;
  department: string;
  salaryRange?: number;
  remoteDays?: number;
  status: JobStatus;
  creatorId?: number;
  creatorName?: string;
  skillsRequired?: string[];
  createdAt?: string;
  publishedAt?: string;
  location?: string;
}

export enum JobStatus {
  DRAFT = 'DRAFT',
  PENDING = 'PENDING',
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  FILLED = 'FILLED'
}
