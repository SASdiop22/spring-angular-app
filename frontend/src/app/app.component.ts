import { Component, OnInit } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'frontend';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    /**
     * Vérification du token au démarrage de l'application
     * Cette logique s'exécute quand l'utilisateur :
     * - Ouvre le site pour la première fois
     * - Fait F5 pour rafraîchir la page
     * - Ferme et réouvre le navigateur (si pas de session expirée)
     */
    this.validateStoredToken();
  }

  /**
   * Valide le token stocké et restaure la session utilisateur si nécessaire
   */
  private validateStoredToken(): void {
    console.log('🔍 AppComponent - Vérification du token au démarrage...');

    // Étape 1 : Vérifier si un token existe dans le stockage
    const token = localStorage.getItem('ACCESS_TOKEN');

    if (!token) {
      console.log('ℹ️ AppComponent - Aucun token trouvé. Utilisateur = invité');
      return; // Cas A : Pas de token = utilisateur non connecté
    }

    console.log('✅ AppComponent - Token trouvé dans le stockage');

    // Étape 2 : Vérifier l'expiration du token
    if (this.authService.isTokenExpired(token)) {
      console.warn('⚠️ AppComponent - Token expiré! Suppression et redirection...');

      // Cas B : Token expiré
      // 1. Supprimer le token
      localStorage.removeItem('ACCESS_TOKEN');
      localStorage.removeItem('USER_ROLE');

      // 2. Réinitialiser les services
      this.authService.logout();

      // 3. Rediriger vers la page de connexion
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url }
      });

      return;
    }

    // Étape 3 : Token valide
    console.log('✅ AppComponent - Token valide! Restauration de la session...');

    // Restaurer le rôle de l'utilisateur depuis le token
    this.authService.restoreSessionFromToken(token);

    console.log('✅ AppComponent - Session utilisateur restaurée avec succès!');
  }
}

