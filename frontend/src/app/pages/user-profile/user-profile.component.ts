import { Component, OnInit, DoCheck } from '@angular/core'
import { Router } from '@angular/router'
import { AuthService } from '../../services/auth.service'
import { RoleService } from '../../services/role.service'

interface UserProfile {
  id: number
  username: string
  email: string
  nom: string
  prenom: string
  telephone: string
  createdAt: string
  isCandidat?: boolean
  ville?: string
  telephone_candidat?: string
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit, DoCheck {
  userProfile: UserProfile | null = null
  loading = true
  error: string | null = null
  editMode = false
  showDeleteModal = false
  deleteConfirmation = ''
  updatingProfile = false
  deletingAccount = false
  isRH = false
  private lastUserId: number | null = null

  // Formulaire d'édition
  editForm = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    ville: '',
    telephone_candidat: ''
  }

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log("📄 UserProfileComponent - ngOnInit()")
    this.isRH = this.roleService.isRH()
    console.log("   isRH from RoleService:", this.isRH)
    this.loadUserProfile()
  }

  /**
   * Vérifie si l'utilisateur a changé et recharge le profil si nécessaire
   */
  ngDoCheck(): void {
    const currentUserId = this.authService.getCurrentUserId()
    if (currentUserId && currentUserId !== this.lastUserId) {
      console.log("👁️ UserProfileComponent.ngDoCheck() - UserId changed:", this.lastUserId, "->", currentUserId)
      this.lastUserId = currentUserId
      this.isRH = this.roleService.isRH()
      this.loadUserProfile()
    }
  }

  /**
   * Charge le profil utilisateur
   */
  loadUserProfile(): void {
    this.loading = true
    this.error = null

    const userId = this.authService.getCurrentUserId()
    const username = this.authService.getCurrentUserName()

    console.log("🔍 UserProfileComponent.loadUserProfile()")
    console.log("   userId:", userId)
    console.log("   username:", username)
    console.log("   isRH:", this.isRH)

    if (!userId || !username) {
      this.error = "Impossible de charger votre profil"
      this.loading = false
      return
    }

    // Créer le profil avec les données réelles du token
    const nom = this.extractNomFromUsername(username)
    const prenom = this.extractPrenomFromUsername(username)

    this.userProfile = {
      id: userId,
      username: username,
      email: `${username}@example.com`,
      nom: nom,
      prenom: prenom,
      telephone: '+33 6 00 00 00 00',
      createdAt: new Date().toISOString().split('T')[0],
      isCandidat: this.authService.isCandidat(),
      ville: '',
      telephone_candidat: ''
    }

    console.log("✅ UserProfileComponent - Profil créé:", this.userProfile)

    // Initialiser le formulaire d'édition
    if (this.userProfile) {
      this.editForm = {
        nom: this.userProfile.nom,
        prenom: this.userProfile.prenom,
        email: this.userProfile.email,
        telephone: this.userProfile.telephone,
        ville: this.userProfile.ville || '',
        telephone_candidat: this.userProfile.telephone_candidat || ''
      }
    }

    this.loading = false
  }

  /**
   * Extrait le prénom du username (avant le point)
   */
  private extractPrenomFromUsername(username: string): string {
    if (!username) return ''
    const parts = username.split('.')
    return parts[0].charAt(0).toUpperCase() + parts[0].slice(1)
  }

  /**
   * Extrait le nom du username (après le point)
   */
  private extractNomFromUsername(username: string): string {
    if (!username) return ''
    const parts = username.split('.')
    if (parts.length > 1) {
      return parts[1].toUpperCase()
    }
    return ''
  }

  /**
   * Active le mode édition
   */
  toggleEditMode(): void {
    this.editMode = !this.editMode
    if (!this.editMode && this.userProfile) {
      // Réinitialiser le formulaire si on annule
      this.editForm = {
        nom: this.userProfile.nom,
        prenom: this.userProfile.prenom,
        email: this.userProfile.email,
        telephone: this.userProfile.telephone,
        ville: this.userProfile.ville || '',
        telephone_candidat: this.userProfile.telephone_candidat || ''
      }
    }
  }

  /**
   * Sauvegarde les modifications du profil
   */
  saveProfile(): void {
    if (!this.userProfile) return

    this.updatingProfile = true
    this.error = null

    // TODO: Appeler un endpoint pour mettre à jour le profil
    // Pour l'instant, on simule juste
    setTimeout(() => {
      this.userProfile = {
        ...this.userProfile!,
        ...this.editForm
      }
      this.editMode = false
      this.updatingProfile = false
      console.log('✅ Profil mis à jour avec succès')
    }, 500)
  }

  /**
   * Ouvre le modal de suppression de compte
   */
  openDeleteModal(): void {
    this.showDeleteModal = true
    this.deleteConfirmation = ''
  }

  /**
   * Ferme le modal de suppression
   */
  closeDeleteModal(): void {
    this.showDeleteModal = false
    this.deleteConfirmation = ''
  }

  /**
   * Supprime le compte utilisateur
   */
  deleteAccount(): void {
    if (this.deleteConfirmation.toUpperCase() !== 'SUPPRIMER MON COMPTE') {
      this.error = "Le texte de confirmation n'est pas correct"
      return
    }

    this.deletingAccount = true
    this.error = null

    // TODO: Appeler un endpoint pour supprimer le compte
    // Pour l'instant, on simule juste
    setTimeout(() => {
      this.authService.logout()
      this.router.navigate(['/login'])
    }, 1000)
  }

  /**
   * Revient à la page précédente
   */
  goBack(): void {
    this.router.navigate(['/'])
  }
}

