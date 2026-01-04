import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthRequest } from '../models/AuthRequest';
import { AuthResponse } from '../models/AuthResponse';
import { Observable, of } from 'rxjs';
import { geturl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(value: AuthRequest): Observable<AuthResponse> {
    // MOCK LOGIN for demo purposes
    return of({ accessToken: 'mock-token-12345' });
    // const url=`${geturl()}/api/auth/login`;
    // return this.http.post<AuthResponse>(url,value);
  }

  authenticated(): boolean {
    return !!sessionStorage.getItem("ACCESS_TOKEN");
  }

}
