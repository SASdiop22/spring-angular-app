import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private roleSubject = new BehaviorSubject<string>('VISITOR');
  public role$: Observable<string> = this.roleSubject.asObservable();

  constructor() {
    // Initialiser avec le rôle stocké en session
    const savedRole = sessionStorage.getItem('USER_ROLE') || 'VISITOR';
    console.log('🔐 RoleService init - Rôle sauvegardé:', savedRole);
    this.roleSubject.next(savedRole);
  }

  setRole(role: string): void {
    console.log('🔐 RoleService.setRole() ->', role);
    this.roleSubject.next(role);
    sessionStorage.setItem('USER_ROLE', role);
  }

  getRole(): string {
    const role = this.roleSubject.value;
    console.log('🔐 RoleService.getRole() ->', role);
    return role;
  }

  isRH(): boolean {
    const role = this.getRole();
    const result = role === 'RH' || role === 'ADMIN';
    console.log('🔐 RoleService.isRH() ->', result, '(role:', role, ')');
    return result;
  }

  isCandidat(): boolean {
    const role = this.getRole();
    const result = role === 'CANDIDAT';
    console.log('🔐 RoleService.isCandidat() ->', result, '(role:', role, ')');
    return result;
  }

  isVisitor(): boolean {
    return this.getRole() === 'VISITOR';
  }

  reset(): void {
    console.log('🔐 RoleService.reset()');
    this.roleSubject.next('VISITOR');
    sessionStorage.removeItem('USER_ROLE');
  }
}

