# GUIDE DE CORRECTION - ERREUR 403 LOGIN

## 📋 ANALYSE DU PROBLÈME

L'erreur **403 (Forbidden)** lors du login indique que la requête POST vers `/api/auth/login` est rejetée par la configuration de sécurité.

### Causes potentielles:

1. **CORS bloqué** - Le frontend (localhost:4200) envoie une requête vers (localhost:8080)
2. **CSRF** - Protection CSRF activée sans token (déjà désactivée dans votre config ✅)
3. **Authentification incomplète** - L'AuthenticationManager n'est pas correctement configuré
4. **Seeders non exécutés** - Les utilisateurs de test n'existent pas en base de données

---

## ✅ SOLUTIONS APPLIQUÉES

### 1. SecurityConfig.java (DÉJÀ CORRECT)
- ✅ CORS configuré pour `http://localhost:4200`
- ✅ CSRF désactivé pour les API REST
- ✅ Session Stateless (JWT)
- ✅ `/api/auth/**` permis sans authentification

### 2. JwtAuthFilter.java (DÉJÀ CORRECT)
- ✅ Laisse passer `/api/auth` sans JWT
- ✅ Vérifie les tokens JWT pour les autres endpoints

---

## 🧪 COMMENT TESTER SANS LE FRONTEND

### Option 1: Utiliser curl (Simple)

```bash
# 1. Démarrer le serveur
cd /Users/abdousamad/Desktop/Projet/backend
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
java -jar target/spring-angular-app.jar

# 2. En parallèle, dans un autre terminal, tester le login:
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice.rh","password":"password123"}' | jq .

# Réponse attendue:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Option 2: Utiliser le script test-login.sh

```bash
chmod +x /Users/abdousamad/Desktop/Projet/test-login.sh
/Users/abdousamad/Desktop/Projet/test-login.sh
```

### Option 3: Utiliser Postman/Insomnia

1. Créer une nouvelle requête POST
2. URL: `http://localhost:8080/api/auth/login`
3. Headers: `Content-Type: application/json`
4. Body (JSON):
```json
{
  "username": "alice.rh",
  "password": "password123"
}
```
5. Envoyer et vérifier la réponse

---

## 🔑 IDENTIFIANTS DE TEST

Créés par le `UserSeeder`:

| Username | Password | Rôle |
|----------|----------|------|
| alice.rh | password123 | RH |
| bob.admin | password123 | ADMIN |
| cathy.employe | password123 | EMPLOYE |
| jean.rgpd | password123 | CANDIDAT |
| marie.hired | password123 | CANDIDAT |
| sophie.onboard | password123 | CANDIDAT |
| dylan.demandeur | password123 | EMPLOYE |
| paul.rejet | password123 | CANDIDAT |

---

## 🔍 VÉRIFICATIONS À FAIRE

### Vérifier la base de données

```bash
# Connecter à PostgreSQL
psql -U abdousamad -d angular-app

# Vérifier les utilisateurs
SELECT username, email, user_type FROM users;

# Vérifier les rôles
SELECT u.username, r.name FROM users u 
JOIN user_roles_map urm ON u.id = urm.user_id 
JOIN user_roles r ON r.id = urm.role_id;
```

### Vérifier les logs du serveur

```bash
# Relancer avec logs DEBUG
export SPRING_PROFILES_ACTIVE=debug
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
java -jar target/spring-angular-app.jar 2>&1 | grep -i "auth\|security\|error"
```

---

## 🛠️ SI VOUS AVEZ ENCORE DES PROBLÈMES

### Le serveur ne démarre pas

1. Vérifier Java 17:
```bash
/opt/homebrew/opt/openjdk@17/bin/java -version
```

2. Vérifier le port:
```bash
lsof -i :8080
```

3. Nettoyer et reconstruire:
```bash
cd /Users/abdousamad/Desktop/Projet/backend
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
mvn clean install -DskipTests
```

### Erreur 403 persiste

1. Vérifier la configuration CORS dans browser DevTools (F12)
   - Onglet Network
   - Chercher la requête POST `/api/auth/login`
   - Regarder les headers `Access-Control-Allow-*`

2. Activer les logs de Spring Security:
   - Modifier `application.properties`:
```properties
spring.security.debug=true
logging.level.org.springframework.security=DEBUG
```

### Les utilisateurs ne sont pas créés

1. Vérifier que les seeders s'exécutent:
   - Chercher dans les logs: "UserSeeder", "EmployeSeeder", "CandidatSeeder"

2. Forcer la réinitialisation de la base:
   - `application.properties` contient déjà: `spring.jpa.hibernate.ddl-auto=create-drop`
   - Cela supprime et recrée les tables à chaque démarrage

---

## 📝 RÉSUMÉ DES ÉTAPES

1. ✅ Installer Java 17:
```bash
brew install openjdk@17
```

2. ✅ Compiler le backend:
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
mvn clean install -DskipTests
```

3. ✅ Démarrer le serveur:
```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
java -jar target/spring-angular-app.jar
```

4. ✅ Tester l'authentification:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice.rh","password":"password123"}'
```

5. ✅ Récupérer le token et l'utiliser dans le frontend:
```javascript
// Dans le composant login
sessionStorage.setItem("ACCESS_TOKEN", response.accessToken);
```

---

## 🎯 PROCHAINES ÉTAPES POUR LE FRONTEND

Une fois que le backend fonctionne:

1. Vérifier que le `AuthService` envoie correctement le login:
```typescript
login(credentials): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(
    `${this.apiUrl}/auth/login`,
    credentials
  );
}
```

2. Vérifier que le JWT Interceptor ajoute le token:
```typescript
// Dans JwtInterceptor
const token = sessionStorage.getItem('ACCESS_TOKEN');
if (token) {
  req = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}
```

3. Ajouter des logs pour déboguer:
```typescript
// Dans login.component.ts
this.authService.login(this.loginForm.value).subscribe({
  next: (response) => {
    console.log('✅ Login réussi:', response);
    sessionStorage.setItem("ACCESS_TOKEN", response.accessToken);
    this.router.navigateByUrl("/");
  },
  error: (error) => {
    console.error('❌ Login échoué:', error);
    this.errorMessage = error.error?.message || 'Erreur de connexion';
  }
});
```

---

Good luck! 🚀

