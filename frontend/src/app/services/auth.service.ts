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
    const savedRole = localStorage.getItem('USER_ROLE');
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
          // Utiliser localStorage au lieu de sessionStorage pour partager entre onglets
          localStorage.setItem("ACCESS_TOKEN", tokenValue);
          // Extraire le rôle du token JWT
          const decodedToken = this.decodeToken(tokenValue);
          console.log('🔑 AuthService.login() - Decoded token:', decodedToken);

          let role = null;

          // 1. Chercher le rôle dans le champ 'role' du token
          if (decodedToken && decodedToken.role) {
            role = decodedToken.role;
            console.log('🔑 AuthService.login() - Role trouvé dans token.role:', role);
          }
          // 2. Chercher dans le champ 'authorities' du token
          else if (decodedToken && decodedToken.authorities) {
            const authorities = Array.isArray(decodedToken.authorities)
              ? decodedToken.authorities
              : [decodedToken.authorities];
            console.log('🔑 AuthService.login() - Authorities trouvées:', authorities);

            if (authorities.some((auth: any) => auth.includes('RH'))) {
              role = 'RH';
            } else if (authorities.some((auth: any) => auth.includes('CANDIDAT'))) {
              role = 'CANDIDAT';
            } else if (authorities.some((auth: any) => auth.includes('ADMIN'))) {
              role = 'ADMIN';
            }
          }
          // 3. Fallback: extraire le rôle du username
          else if (decodedToken && decodedToken.sub) {
            const username = decodedToken.sub.toLowerCase();
            console.log('🔑 AuthService.login() - Extraction du rôle du username:', username);

            // Chercher les patterns dans le username
            if (username.includes('.rh') || username.includes('rh')) {
              role = 'RH';
            } else if (username.includes('.candidat') || username.includes('candidat') ||
                       username.includes('.candidate') || username.includes('candidate') ||
                       username.includes('candidate_')) {
              role = 'CANDIDAT';
            } else if (username.includes('.admin') || username.includes('admin')) {
              role = 'ADMIN';
            }
            // Si le username contient 'rgpd', c'est un candidat
            else if (username.includes('rgpd')) {
              role = 'CANDIDAT';
              console.log('🔑 AuthService.login() - Username contient RGPD, assigné en CANDIDAT');
            }
            // Default : si connecté et aucun pattern trouvé, assigner CANDIDAT
            else {
              role = 'CANDIDAT';
              console.log('🔑 AuthService.login() - Assigné par défaut en CANDIDAT');
            }

            console.log('🔑 AuthService.login() - Role extrait du username:', role);
          }

          if (role) {
            console.log('🔑 AuthService.login() - Setting role:', role);
            this.roleService.setRole(role);
          } else {
            console.warn('⚠️ AuthService.login() - Impossible de trouver le rôle!', decodedToken);
            // Par défaut, assigner CANDIDAT si authentifié
            this.roleService.setRole('CANDIDAT');
          }
        }
      })
    );
  }

  authenticated(): boolean {
    return !!localStorage.getItem("ACCESS_TOKEN");
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

  /**
   * Récupère l'ID de l'utilisateur connecté depuis le token JWT
   */
  getCurrentUserId(): number | null {
    const token = localStorage.getItem("ACCESS_TOKEN");
    if (!token) {
      console.warn('⚠️ AuthService.getCurrentUserId() - No token found');
      return null;
    }

    const decodedToken = this.decodeToken(token);
    if (!decodedToken) {
      console.warn('⚠️ AuthService.getCurrentUserId() - Could not decode token');
      return null;
    }

    console.log('🔍 AuthService.getCurrentUserId() - Decoded token:', decodedToken);

    // Chercher l'ID dans le token - priorité: userId, puis id, puis user_id
    let userId = decodedToken.userId || decodedToken.id || decodedToken.user_id;

    if (userId && typeof userId === 'number') {
      console.log('👤 AuthService.getCurrentUserId() - Found userId:', userId);
      return userId;
    }

    console.warn('⚠️ AuthService.getCurrentUserId() - No valid userId found in token');
    return null;
  }

  /**
   * Récupère le username (sub) du token JWT
   */
  getCurrentUserName(): string | null {
    const token = localStorage.getItem("ACCESS_TOKEN");
    if (!token) {
      return null;
    }

    const decodedToken = this.decodeToken(token);
    if (decodedToken && decodedToken.sub) {
      console.log('👤 AuthService.getCurrentUserName() - Found username:', decodedToken.sub);
      return decodedToken.sub;
    }

    console.warn('⚠️ AuthService.getCurrentUserName() - Could not decode token or no sub found');
    return null;
  }

  /**
   * Vérifie si un token JWT est expiré
   * @param token - Le token JWT à vérifier
   * @returns true si le token est expiré, false sinon
   */
  isTokenExpired(token: string): boolean {
    try {
      const decodedToken = this.decodeToken(token);

      if (!decodedToken || !decodedToken.exp) {
        console.warn('⚠️ AuthService.isTokenExpired() - Token invalide ou sans expiration');
        return true; // Considérer comme expiré si on ne peut pas le décoder
      }

      // exp est en secondes, Date.now() est en millisecondes
      const expirationTime = decodedToken.exp * 1000;
      const now = Date.now();
      const isExpired = now > expirationTime;

      console.log(`🔔 AuthService.isTokenExpired() - Expiration: ${new Date(expirationTime).toLocaleString()}, Maintenant: ${new Date(now).toLocaleString()}, Expiré: ${isExpired}`);

      return isExpired;
    } catch (error) {
      console.error('❌ AuthService.isTokenExpired() - Erreur lors de la vérification:', error);
      return true; // Considérer comme expiré en cas d'erreur
    }
  }

  /**
   * Restaure la session utilisateur à partir du token stocké
   * Extrait le rôle et l'ID utilisateur et les stocke dans les services
   * @param token - Le token JWT
   */
  restoreSessionFromToken(token: string): void {
    try {
      const decodedToken = this.decodeToken(token);

      if (!decodedToken) {
        console.warn('⚠️ AuthService.restoreSessionFromToken() - Impossible de décoder le token');
        return;
      }

      console.log('🔄 AuthService.restoreSessionFromToken() - Décoded token:', decodedToken);

      // 1. Restaurer le rôle
      let role = null;

      // Chercher dans le champ 'role' du token
      if (decodedToken.role) {
        role = decodedToken.role;
        console.log('✅ AuthService.restoreSessionFromToken() - Role trouvé:', role);
      }
      // Chercher dans le champ 'authorities'
      else if (decodedToken.authorities) {
        const authorities = Array.isArray(decodedToken.authorities)
          ? decodedToken.authorities
          : [decodedToken.authorities];

        if (authorities.some((auth: any) => auth.includes('RH'))) {
          role = 'RH';
        } else if (authorities.some((auth: any) => auth.includes('CANDIDAT'))) {
          role = 'CANDIDAT';
        } else if (authorities.some((auth: any) => auth.includes('ADMIN'))) {
          role = 'ADMIN';
        }
        console.log('✅ AuthService.restoreSessionFromToken() - Role trouvé dans authorities:', role);
      }
      // Fallback: extraire du username
      else if (decodedToken.sub) {
        const username = decodedToken.sub.toLowerCase();

        if (username.includes('.rh') || username.includes('rh')) {
          role = 'RH';
        } else if (username.includes('.candidat') || username.includes('candidat') ||
                   username.includes('.candidate') || username.includes('candidate') ||
                   username.includes('candidate_') || username.includes('rgpd')) {
          role = 'CANDIDAT';
        } else if (username.includes('.admin') || username.includes('admin')) {
          role = 'ADMIN';
        } else {
          role = 'CANDIDAT'; // Par défaut
        }
        console.log('✅ AuthService.restoreSessionFromToken() - Role extrait du username:', role);
      }

      // Définir le rôle s'il a été trouvé
      if (role) {
        this.roleService.setRole(role);
        console.log('✅ AuthService.restoreSessionFromToken() - Rôle restauré:', role);
      } else {
        console.warn('⚠️ AuthService.restoreSessionFromToken() - Impossible de trouver le rôle');
      }

    } catch (error) {
      console.error('❌ AuthService.restoreSessionFromToken() - Erreur:', error);
    }
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
    localStorage.removeItem("ACCESS_TOKEN");
    this.roleService.reset();
  }
}
