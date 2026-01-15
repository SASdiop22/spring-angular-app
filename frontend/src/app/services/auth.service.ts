import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthRequest } from '../models/AuthRequest';
import { AuthResponse } from '../models/AuthResponse';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { geturl } from '../../environments/environment';
import { RoleService } from './role.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private roleService: RoleService
  ) {
    // Initialiser le rôle au démarrage de l'app
    this.initializeRoleFromStorage();
  }

  private initializeRoleFromStorage(): void {
    const savedRole = sessionStorage.getItem('USER_ROLE');
    if (savedRole) {
      this.roleService.setRole(savedRole);
    }
  }

  login(value: AuthRequest): Observable<AuthResponse> {
    const url = `${geturl()}/api/auth/login`;
    return this.http.post<AuthResponse>(url, value).pipe(
      tap(response => {
        // Support both 'token' and 'accessToken' properties
        const tokenValue = response.token || response.accessToken;
        console.log('🔑 AuthService.login() - Response:', response);
        console.log('🔑 AuthService.login() - Token:', tokenValue);
        if (response && tokenValue) {
          sessionStorage.setItem("ACCESS_TOKEN", tokenValue);
          // Extraire le rôle du token JWT
          const decodedToken = this.decodeToken(tokenValue);
          console.log('🔑 AuthService.login() - Decoded token:', decodedToken);

          let role = null;

          // Chercher le rôle dans le token (idéalement)
          if (decodedToken && decodedToken.role) {
            role = decodedToken.role;
            console.log('🔑 AuthService.login() - Role trouvé dans token:', role);
          }
          // Fallback: extraire le rôle du username (alice.rh -> RH)
          else if (decodedToken && decodedToken.sub) {
            const username = decodedToken.sub;
            console.log('🔑 AuthService.login() - Extraction du rôle du username:', username);

            if (username.includes('.rh')) {
              role = 'RH';
            } else if (username.includes('.candidat') || username.includes('.candidate')) {
              role = 'CANDIDAT';
            } else if (username.includes('.admin')) {
              role = 'ADMIN';
            }
            console.log('🔑 AuthService.login() - Role extrait du username:', role);
          }

          if (role) {
            console.log('🔑 AuthService.login() - Setting role:', role);
            this.roleService.setRole(role);
          } else {
            console.warn('⚠️ AuthService.login() - Impossible de trouver le rôle!', decodedToken);
          }
        }
      })
    );
  }

  authenticated(): boolean {
    return !!sessionStorage.getItem("ACCESS_TOKEN");
  }

  getUserRole(): string {
    return this.roleService.getRole();
  }

  isRH(): boolean {
    return this.roleService.isRH();
  }

  isCandidat(): boolean {
    return this.roleService.isCandidat();
  }

  isAdmin(): boolean {
    return this.roleService.getRole() === "ADMIN";
  }

  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Erreur lors du décodage du token:', error);
      return null;
    }
  }

  logout(): void {
    sessionStorage.removeItem("ACCESS_TOKEN");
    this.roleService.reset();
  }
}
