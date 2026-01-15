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
}
