import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Application } from '../models/application.model';
import { geturl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {

  private readonly baseUrl = `${geturl()}/api/applications`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Application[]> {
    return this.http.get<Application[]>(this.baseUrl);
  }

  apply(jobOfferId: number, candidateId: number, cvUrl: string, coverLetter?: string): Observable<string> {
    const params: any = { jobOfferId, candidateId, cvUrl };
    if (coverLetter) {
      params.coverLetter = coverLetter;
    }
    return this.http.post(`${this.baseUrl}/apply`, null, { 
      params, 
      responseType: 'text' 
    });
  }
}
