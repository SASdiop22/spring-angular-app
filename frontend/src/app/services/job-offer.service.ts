import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JobOffer } from '../models/job-offer.model';
import { geturl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JobOfferService {

  private readonly baseUrl = `${geturl()}/api/joboffers`;

  constructor(private http: HttpClient) {}

  getAllPublished(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.baseUrl);
  }

  getById(id: number): Observable<JobOffer> {
    return this.http.get<JobOffer>(`${this.baseUrl}/${id}`);
  }

  search(keyword: string): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(`${this.baseUrl}/search`, { params: { keyword } });
  }

  create(jobOffer: Partial<JobOffer>): Observable<JobOffer> {
    return this.http.post<JobOffer>(this.baseUrl, jobOffer);
  }

  update(id: number, jobOffer: Partial<JobOffer>): Observable<JobOffer> {
    return this.http.put<JobOffer>(`${this.baseUrl}/${id}`, jobOffer);
  }

  submitForApproval(id: number): Observable<JobOffer> {
    return this.http.patch<JobOffer>(`${this.baseUrl}/${id}/submit`, {});
  }

  publishOffer(id: number, salary: number, remoteDays: number): Observable<JobOffer> {
    return this.http.patch<JobOffer>(`${this.baseUrl}/${id}/publish`, null, {
      params: { salary: salary.toString(), remoteDays: remoteDays.toString() }
    });
  }

  closeOffer(id: number): Observable<JobOffer> {
    return this.http.patch<JobOffer>(`${this.baseUrl}/${id}/close`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getAllWithPrivilege(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(`${this.baseUrl}/privilege`);
  }
}
