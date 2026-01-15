#!/bin/bash

# Script pour tester l'authentification sans le frontend

echo "=== TEST D'AUTHENTIFICATION SANS FRONTEND ==="
echo ""

# Démarrer le serveur
echo "1. Démarrage du serveur..."
cd /Users/abdousamad/Desktop/Projet/backend
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
java -jar target/spring-angular-app.jar > /tmp/spring-test.log 2>&1 &
SERVER_PID=$!
echo "Serveur démarré avec PID: $SERVER_PID"

# Attendre que le serveur démarre
echo "2. Attente du démarrage du serveur (10 secondes)..."
sleep 10

# Vérifier que le serveur est prêt
echo "3. Vérification de l'état du serveur..."
HEALTH=$(curl -s http://localhost:8080/actuator/health 2>/dev/null)
if [ -z "$HEALTH" ]; then
    echo "❌ Le serveur n'a pas démarré correctement"
    tail -50 /tmp/spring-test.log
    kill $SERVER_PID
    exit 1
fi
echo "✅ Serveur prêt"

# Test 1: Login avec alice.rh
echo ""
echo "4. TEST 1: Login avec alice.rh / password123"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice.rh","password":"password123"}')

echo "Réponse:"
echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"

# Extraire le token si succès
TOKEN=$(echo "$RESPONSE" | jq -r '.accessToken' 2>/dev/null)
if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
    echo "✅ Login réussi!"
    echo "Token: $TOKEN"

    # Test 2: Utiliser le token pour accéder à une ressource protégée
    echo ""
    echo "5. TEST 2: Accès à une ressource protégée avec le token"
    curl -s -X GET http://localhost:8080/api/users/me \
      -H "Authorization: Bearer $TOKEN" | jq .
else
    echo "❌ Login échoué!"
fi

# Cleanup
echo ""
echo "6. Arrêt du serveur..."
kill $SERVER_PID
echo "Test terminé"

