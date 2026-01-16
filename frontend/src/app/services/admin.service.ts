import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import type { Observable } from "rxjs"
import { geturl } from "../../environments/environment"

@Injectable({
  providedIn: "root",
})
export class AdminService {
  private apiUrl = `${geturl()}/api`

  constructor(private http: HttpClient) {}

  /**
   * Récupère tous les utilisateurs (Admin seulement)
   */
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`)
  }

  /**
   * Récupère un utilisateur par ID (Admin ou RH)
   */
  getUserById(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/users/${userId}`)
  }

  /**
   * Supprime définitivement un utilisateur (Admin seulement)
   */
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`)
  }

  /**
   * Récupère toutes les offres d'emploi avec tous les statuts (Admin et RH)
   */
  getAllJobOffers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/joboffers/privilege`)
  }

  /**
   * Supprime définitivement une offre d'emploi (Admin seulement)
   */
  deleteJobOffer(jobOfferId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/joboffers/${jobOfferId}`)
  }

  /**
   * Archive une offre d'emploi (Admin et RH)
   */
  archiveJobOffer(jobOfferId: number): Observable<any> {
    return this.http.patch<any>(
      `${this.apiUrl}/joboffers/${jobOfferId}/status?status=CLOSED`,
      {}
    )
  }
}

