import { Injectable } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import type { Observable } from "rxjs"
import { geturl } from "../../environments/environment"
import type { Application } from "../models/Application"

@Injectable({
  providedIn: "root",
})
export class ApplicationService {
  private apiUrl = `${geturl()}/api/applications`

  constructor(private http: HttpClient) {}

  apply(jobOfferId: number, candidateId: number, cvUrl: string, coverLetter?: string): Observable<Application> {
    const params = new HttpParams()
      .set("jobOfferId", jobOfferId.toString())
      .set("candidateId", candidateId.toString())
      .set("cvUrl", cvUrl)
      .set("coverLetter", coverLetter || "")

    return this.http.post<Application>(`${this.apiUrl}/apply`, null, { params })
  }

  /**
   * Postule à une offre en uploadant les fichiers CV et LM
   * Envoie les fichiers en multipart/form-data
   */
  applyWithFiles(jobOfferId: number, candidateId: number, cvFile: File, coverLetterFile?: File): Observable<Application> {
    const formData = new FormData();
    formData.append('jobOfferId', jobOfferId.toString());
    formData.append('candidateId', candidateId.toString());
    formData.append('cvFile', cvFile);

    if (coverLetterFile) {
      formData.append('coverLetterFile', coverLetterFile);
    }

    return this.http.post<Application>(`${this.apiUrl}/apply-with-files`, formData);
  }

  getApplicationById(id: number): Observable<Application> {
    return this.http.get<Application>(`${this.apiUrl}/${id}`)
  }

  getApplicationsByCandidate(candidateId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.apiUrl}/candidate/${candidateId}`)
  }

  getAllApplications(): Observable<Application[]> {
    return this.http.get<Application[]>(this.apiUrl)
  }

  /**
   * Récupère les candidatures pour une offre d'emploi spécifique
   * Triées automatiquement par score de correspondance décroissant
   */
  getApplicationsByJobOffer(jobOfferId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${geturl()}/api/joboffers/${jobOfferId}/candidates`)
  }
}
