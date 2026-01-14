import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, DecodedToken } from '../models/user.model';
import { geturl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'ACCESS_TOKEN';
  private readonly USER_KEY = 'CURRENT_USER';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: AuthRequest): Observable<AuthResponse> {
    const url = `${geturl()}/api/auth/login`;
    return this.http.post<AuthResponse>(url, credentials).pipe(
      tap(response => {
        this.setToken(response.accessToken);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  register(credentials: AuthRequest): Observable<string> {
    const url = `${geturl()}/api/auth/register`;
    return this.http.post(url, credentials, { responseType: 'text' });
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_KEY);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  setToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const decoded = this.decodeToken(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  decodeToken(token: string): DecodedToken {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  }

  getCurrentUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded = this.decodeToken(token);
      return decoded.sub;
    } catch {
      return null;
    }
  }

  getUserRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];
    try {
      const decoded = this.decodeToken(token);
      return decoded.roles || [];
    } catch {
      return [];
    }
  }

  hasRole(role: string): boolean {
    return this.getUserRoles().includes(role);
  }
}
