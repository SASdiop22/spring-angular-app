import { CanActivateFn, Router, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthService } from "../services/auth.service";
import { inject } from "@angular/core";

export const RHGuard: CanActivateFn = (
):
  Observable<boolean | UrlTree>
  | Promise<boolean | UrlTree>
  | boolean
  | UrlTree => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.authenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.isRH()) {
    return true;
  }

  // Rediriger vers la page d'accueil si l'utilisateur n'est pas RH
  return router.createUrlTree(['/']);
};

export const AdminGuard: CanActivateFn = (
):
  Observable<boolean | UrlTree>
  | Promise<boolean | UrlTree>
  | boolean
  | UrlTree => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.authenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.isAdmin()) {
    return true;
  }

  // Rediriger vers la page d'accueil si l'utilisateur n'est pas Admin
  return router.createUrlTree(['/']);
};

export const CandicatGuard: CanActivateFn = (
):
  Observable<boolean | UrlTree>
  | Promise<boolean | UrlTree>
  | boolean
  | UrlTree => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.authenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authService.isCandidat()) {
    return true;
  }

  // Rediriger vers la page d'accueil si l'utilisateur n'est pas candidat
  return router.createUrlTree(['/']);
};

