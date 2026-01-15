import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { AuthResponse } from '../../models/AuthResponse';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    console.log('Tentative de soumission...');

    if (this.loginForm.invalid) {
      console.log('Formulaire invalide !', this.loginForm);
      return;
    }

    console.log('Formulaire valide, envoi des données...');
    const credentials = {
      username: this.loginForm.value.username?.trim() || '',
      password: this.loginForm.value.password?.trim() || ''
    };
    console.log('Credentials:', credentials);

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(credentials).subscribe({
      next: (response: AuthResponse) => {
        console.log('✅ Login réussi:', response);
        console.log('Token reçu:', response.accessToken);
        sessionStorage.setItem("ACCESS_TOKEN", response.accessToken);
        this.router.navigateByUrl("/");
      },
      error: (error) => {
        console.error('❌ Erreur de login:', error);
        console.error('Status:', error.status);
        console.error('Message:', error.error?.message);

        this.errorMessage = error.error?.message || 'Erreur de connexion';

        if (error.status === 0) {
          this.errorMessage = 'Impossible de se connecter au serveur. Assurez-vous que le backend fonctionne sur http://localhost:8080';
        } else if (error.status === 403) {
          this.errorMessage = 'Nom d\'utilisateur ou mot de passe incorrect';
        } else if (error.status === 401) {
          this.errorMessage = 'Authentification requise';
        }

        this.isLoading = false;
      }
    });
  }
}
