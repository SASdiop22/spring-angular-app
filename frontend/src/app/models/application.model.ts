export interface Application {
  id: number;
  status: string;
  applicationDate: string;
  comment?: string;
  candidateId: number;
  candidateName?: string;
  jobOfferId: number;
  jobOfferTitle?: string;
  cvId?: number;
}

export enum ApplicationStatus {
  PENDING = 'PENDING',
  REVIEWED = 'REVIEWED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
