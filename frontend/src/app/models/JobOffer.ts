export interface JobOffer {
  id: number
  title: string
  description: string
  deadline: string
  department: string
  salaryRange?: number
  salary?: number
  contractType?: string
  remoteDays?: number | null
  status: "DRAFT" | "PENDING" | "OPEN" | "CLOSED" | "FILLED"
  creatorId: number
  creatorName: string
  skillsRequired: string[]
  createdAt: string
  publishedAt?: string
  location: string
}
