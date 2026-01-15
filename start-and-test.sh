#!/bin/bash
set -e

echo "================================"
echo "DÉMARRAGE DU SERVEUR ET TEST"
echo "================================"

# Tuer les anciens processus
echo "1. Arrêt des anciens processus..."
pkill -9 -f "java -jar" || true
sleep 2

# Compiler
echo "2. Compilation du backend..."
cd /Users/abdousamad/Desktop/Projet/backend
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
mvn clean install -DskipTests -q

# Démarrer le serveur
echo "3. Démarrage du serveur..."
java -jar target/spring-angular-app.jar &
SERVER_PID=$!
echo "   Serveur PID: $SERVER_PID"

# Attendre le démarrage
echo "4. Attente du démarrage (15 secondes)..."
sleep 15

# Vérifier que le serveur est prêt
echo "5. Vérification de l'état du serveur..."
if ! curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "❌ Le serveur n'est pas prêt!"
    kill $SERVER_PID || true
    exit 1
fi
echo "✅ Serveur prêt"

# Test du login
echo ""
echo "6. TEST DU LOGIN..."
echo "   URL: http://localhost:8080/api/auth/login"
echo "   User: alice.rh / password123"
echo ""

RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice.rh","password":"password123"}')

echo "Réponse du serveur:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"

# Vérifier le token
TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('accessToken', ''))" 2>/dev/null || echo "")

if [ -n "$TOKEN" ]; then
    echo ""
    echo "✅ LOGIN RÉUSSI!"
    echo "Token: $TOKEN"
    echo ""
    echo "7. Le serveur fonctionne correctement!"
else
    echo ""
    echo "❌ LOGIN ÉCHOUÉ!"
    echo "Vérifiez les logs du serveur"
fi

# Garder le serveur actif
echo ""
echo "Le serveur est actif. Appuyez sur Ctrl+C pour arrêter."
wait $SERVER_PID

