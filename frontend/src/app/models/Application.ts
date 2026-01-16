export interface Application {
  id: number
  status: string
  applicationDate: string
  candidateName: string
  candidateId: number
  jobOfferId: number
  jobOfferTitle: string
  cvUrl: string
  coverLetter?: string
  meetingDate?: string
  rejectionReason?: string
  createdAt: string
  matchingScore?: number
}
