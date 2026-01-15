import { HttpClient } from "@angular/common/http"
import { Injectable } from "@angular/core"
import type { Observable } from "rxjs"
import { geturl } from "../../environments/environment"
import type { JobOffer } from "../models/JobOffer"

@Injectable({
  providedIn: "root",
})
export class JobOfferService {
  private apiUrl = `${geturl()}/api/joboffers`

  constructor(private http: HttpClient) {}

  // Récupère toutes les offres OPEN (publiques)
  getAllPublished(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.apiUrl)
  }

  // Récupère une offre par ID
  getById(id: number): Observable<JobOffer> {
    return this.http.get<JobOffer>(`${this.apiUrl}/${id}`)
  }

  // Recherche d'offres par mot-clé
  search(keyword: string): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(`${this.apiUrl}/search`, {
      params: { keyword },
    })
  }

  // Crée une nouvelle offre d'emploi (pour RH)
  createJobOffer(jobOffer: any): Observable<JobOffer> {
    return this.http.post<JobOffer>(this.apiUrl, jobOffer)
  }

  // Récupère les offres de l'utilisateur connecté (RH)
  getMyJobOffers(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(`${this.apiUrl}/my-offers`)
  }

  // Modifie une offre d'emploi
  updateJobOffer(id: number, jobOffer: any): Observable<JobOffer> {
    return this.http.put<JobOffer>(`${this.apiUrl}/${id}`, jobOffer)
  }

  // Supprime une offre d'emploi
  deleteJobOffer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }
}
