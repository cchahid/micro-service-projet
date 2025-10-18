# Guide pour tester la communication entre les microservices avec Postman

Ce guide vous explique comment tester la communication entre le User Service et le Dinner Service en utilisant Postman.

## Configuration de base

### URLs des services
- **User Service**: `http://localhost:8080`
- **Dinner Service**: `http://localhost:8081`

### Headers communs
- **Content-Type**: `application/json`

## Test du User Service

Avant de tester le Dinner Service, vous devez créer des utilisateurs dans le User Service et obtenir un token d'authentification.

### 1. Créer un utilisateur hôte (Create Host User)

- **Méthode**: POST
- **URL**: `http://localhost:8080/api/auth/signUp/Host`
- **Corps de la requête**:
```json
{
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "password": "password123"
}
```
- **Réponse attendue**: Code 200 (OK) avec les détails de l'utilisateur créé, incluant l'ID. Notez cet ID pour les tests suivants.

### 2. Créer un utilisateur invité (Create Guest User)

- **Méthode**: POST
- **URL**: `http://localhost:8080/api/auth/signUp/Guest`
- **Corps de la requête**:
```json
{
    "nom": "Martin",
    "prenom": "Sophie",
    "email": "sophie.martin@example.com",
    "password": "password123"
}
```
- **Réponse attendue**: Code 200 (OK) avec les détails de l'utilisateur créé.

### 3. Authentification (Login)

- **Méthode**: POST
- **URL**: `http://localhost:8080/api/auth/login`
- **Corps de la requête**:
```json
{
    "email": "jean.dupont@example.com",
    "password": "password123"
}
```
- **Réponse attendue**: Code 200 (OK) avec un token JWT. Notez ce token pour l'utiliser dans les requêtes suivantes.

### 4. Vérifier si un utilisateur est un hôte (Check if User is Host)

- **Méthode**: GET
- **URL**: `http://localhost:8080/api/users/{id}/isHost` (remplacez `{id}` par l'ID de l'utilisateur)
- **Réponse attendue**: `true` pour un utilisateur avec le rôle HOST, `false` sinon.

## Test du Dinner Service

Maintenant que vous avez créé des utilisateurs et obtenu un token, vous pouvez tester le Dinner Service.

### 1. Créer un dîner avec un hôte valide (Create Dinner with Valid Host)

- **Méthode**: POST
- **URL**: `http://localhost:8081/api/dinners`
- **Headers**: 
  - `Content-Type`: `application/json`
  - `Authorization`: `Bearer {votre_token_jwt}` (remplacez par le token obtenu lors de l'authentification)
- **Corps de la requête**:
```json
{
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Français",
    "description": "Un dîner avec des plats français traditionnels",
    "price": 25.99,
    "startTime": "2023-12-15T19:00:00",
    "endTime": "2023-12-15T22:00:00",
    "address": "123 Rue de Paris, Paris, Île-de-France, 75001, France",
    "cuisineType": "Française",
    "maxGuestCount": 10
}
```
- **Réponse attendue**: Code 201 (Created) avec les détails du dîner créé, y compris l'ID et le statut.

### 1.1 Tester la validation de l'hôte (Test Host Validation)

Pour tester la validation de l'hôte, essayez de créer un dîner avec un ID d'hôte qui n'existe pas ou qui n'a pas le rôle HOST:

- **Méthode**: POST
- **URL**: `http://localhost:8081/api/dinners`
- **Headers**: 
  - `Content-Type`: `application/json`
  - `Authorization`: `Bearer {votre_token_jwt}`
- **Corps de la requête**:
```json
{
    "hostId": 999,
    "menuId": 1,
    "name": "Dîner Français",
    "description": "Un dîner avec des plats français traditionnels",
    "price": 25.99,
    "startTime": "2023-12-15T19:00:00",
    "endTime": "2023-12-15T22:00:00",
    "address": "123 Rue de Paris, Paris, Île-de-France, 75001, France",
    "cuisineType": "Française",
    "maxGuestCount": 10
}
```
- **Réponse attendue**: Code 400 (Bad Request) avec un message d'erreur indiquant que l'hôte n'existe pas ou n'a pas le rôle HOST.

### 2. Mettre à jour un dîner avec un hôte valide (Update Dinner with Valid Host)

- **Méthode**: PUT
- **URL**: `http://localhost:8081/api/dinners/{id}` (remplacez `{id}` par l'ID du dîner)
- **Headers**: 
  - `Content-Type`: `application/json`
  - `Authorization`: `Bearer {votre_token_jwt}`
- **Corps de la requête**:
```json
{
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Français Mis à Jour",
    "description": "Un dîner avec des plats français traditionnels et des vins",
    "price": 29.99,
    "startTime": "2023-12-15T19:00:00",
    "endTime": "2023-12-15T22:30:00",
    "address": "123 Rue de Paris, Paris, Île-de-France, 75001, France",
    "cuisineType": "Française",
    "maxGuestCount": 12
}
```
- **Réponse attendue**: Code 200 (OK) avec les détails du dîner mis à jour.

### 2.1 Tester la validation de l'hôte lors de la mise à jour (Test Host Validation on Update)

Pour tester la validation de l'hôte lors de la mise à jour, essayez de mettre à jour un dîner avec un ID d'hôte qui n'existe pas ou qui n'a pas le rôle HOST:

- **Méthode**: PUT
- **URL**: `http://localhost:8081/api/dinners/{id}` (remplacez `{id}` par l'ID du dîner)
- **Headers**: 
  - `Content-Type`: `application/json`
  - `Authorization`: `Bearer {votre_token_jwt}`
- **Corps de la requête**:
```json
{
    "hostId": 999,
    "menuId": 1,
    "name": "Dîner Français Mis à Jour",
    "description": "Un dîner avec des plats français traditionnels et des vins",
    "price": 29.99,
    "startTime": "2023-12-15T19:00:00",
    "endTime": "2023-12-15T22:30:00",
    "address": "123 Rue de Paris, Paris, Île-de-France, 75001, France",
    "cuisineType": "Française",
    "maxGuestCount": 12
}
```
- **Réponse attendue**: Code 400 (Bad Request) avec un message d'erreur indiquant que l'hôte n'existe pas ou n'a pas le rôle HOST.

### 3. Supprimer un dîner (Delete Dinner)

- **Méthode**: DELETE
- **URL**: `http://localhost:8081/api/dinners/{id}` (remplacez `{id}` par l'ID du dîner)
- **Réponse attendue**: Code 204 (No Content)

### 4. Obtenir un dîner par ID (Get Dinner by ID)

- **Méthode**: GET
- **URL**: `http://localhost:8081/api/dinners/{id}` (remplacez `{id}` par l'ID du dîner)
- **Réponse attendue**: Code 200 (OK) avec les détails du dîner.

### 5. Obtenir tous les dîners (Get All Dinners)

- **Méthode**: GET
- **URL**: `http://localhost:8081/api/dinners`
- **Réponse attendue**: Code 200 (OK) avec une liste de tous les dîners.

### 6. Obtenir les dîners par ID d'hôte (Get Dinners by Host ID)

- **Méthode**: GET
- **URL**: `http://localhost:8081/api/dinners/host/{hostId}` (remplacez `{hostId}` par l'ID de l'hôte)
- **Réponse attendue**: Code 200 (OK) avec une liste des dîners de cet hôte.

### 7. Obtenir les dîners par ID de menu (Get Dinners by Menu ID)

- **Méthode**: GET
- **URL**: `http://localhost:8081/api/dinners/menu/{menuId}` (remplacez `{menuId}` par l'ID du menu)
- **Réponse attendue**: Code 200 (OK) avec une liste des dîners utilisant ce menu.

## Guide étape par étape pour Postman

1. **Ouvrir Postman**
2. **Créer une nouvelle collection** (cliquez sur "New" > "Collection")
   - Nommez-la "Dinner Service API"
3. **Ajouter une nouvelle requête** (cliquez sur "..." à côté du nom de la collection > "Add Request")
4. **Configurer la requête**:
   - Sélectionnez la méthode HTTP appropriée (GET, POST, PUT, DELETE)
   - Entrez l'URL complète
   - Pour les requêtes POST et PUT, allez dans l'onglet "Body", sélectionnez "raw" et "JSON", puis entrez le corps de la requête
5. **Envoyer la requête** (cliquez sur "Send")
6. **Vérifier la réponse** dans le panneau inférieur

## Exemples de valeurs pour les tests

### Exemple de création de dîner (POST)

```json
{
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Italien",
    "description": "Un dîner avec des plats italiens authentiques",
    "price": 22.50,
    "startTime": "2023-12-20T18:30:00",
    "endTime": "2023-12-20T21:30:00",
    "address": "45 Avenue de l'Italie, Lyon, Auvergne-Rhône-Alpes, 69003, France",
    "cuisineType": "Italienne",
    "maxGuestCount": 8
}
```

### Exemple de réponse attendue

```json
{
    "id": 1,
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Italien",
    "description": "Un dîner avec des plats italiens authentiques",
    "price": 22.5,
    "startTime": "2023-12-20T18:30:00",
    "endTime": "2023-12-20T21:30:00",
    "address": "45 Avenue de l'Italie, Lyon, Auvergne-Rhône-Alpes, 69003, France",
    "cuisineType": "Italienne",
    "maxGuestCount": 8,
    "status": "UPCOMING"
}
```

## Résolution des problèmes courants

### 1. Les services ne répondent pas

- **Problème**: Les requêtes vers les services échouent avec des erreurs de connexion.
- **Solution**: 
  - Vérifiez que les deux services (User Service et Dinner Service) sont bien démarrés.
  - Vérifiez que les ports 8080 (User Service) et 8081 (Dinner Service) sont disponibles et non utilisés par d'autres applications.
  - Utilisez les commandes `curl http://localhost:8080/actuator/health` et `curl http://localhost:8081/actuator/health` pour vérifier l'état des services.

### 2. Erreurs d'authentification

- **Problème**: Les requêtes échouent avec des erreurs 401 (Unauthorized) ou 403 (Forbidden).
- **Solution**:
  - Assurez-vous d'inclure le token JWT dans l'en-tête Authorization avec le format correct: `Bearer {token}`.
  - Vérifiez que le token n'a pas expiré (généralement après 24 heures). Si c'est le cas, reconnectez-vous pour obtenir un nouveau token.
  - Assurez-vous d'utiliser le bon token pour l'utilisateur avec les droits appropriés.

### 3. Erreurs de validation de l'hôte

- **Problème**: La création ou la mise à jour d'un dîner échoue avec une erreur indiquant que l'hôte n'existe pas ou n'a pas le rôle HOST.
- **Solution**:
  - Vérifiez que l'ID de l'hôte utilisé correspond à un utilisateur existant dans le User Service.
  - Vérifiez que cet utilisateur a bien le rôle HOST en utilisant l'endpoint `/api/users/{id}/isHost`.
  - Assurez-vous que le User Service est en cours d'exécution et accessible depuis le Dinner Service.

### 4. Erreurs de format de données

- **Problème**: Les requêtes échouent avec des erreurs 400 (Bad Request) liées au format des données.
- **Solution**:
  - Les dates doivent être au format ISO-8601 (YYYY-MM-DDThh:mm:ss).
  - L'adresse doit être au format: "rue, ville, état/région, code postal, pays". Tous les cinq éléments sont obligatoires et doivent être séparés par des virgules.
  - Vérifiez que tous les champs obligatoires sont présents et correctement formatés.

### 5. Problèmes de communication entre services

- **Problème**: Le Dinner Service ne parvient pas à communiquer avec le User Service.
- **Solution**:
  - Vérifiez les logs du Dinner Service pour voir les erreurs de communication.
  - Assurez-vous que l'URL du User Service est correctement configurée dans `application.properties` du Dinner Service.
  - Vérifiez qu'il n'y a pas de problèmes réseau ou de pare-feu bloquant la communication entre les services.

## Notes importantes

- Assurez-vous que les deux services (User Service sur le port 8080 et Dinner Service sur le port 8081) sont en cours d'exécution avant de commencer les tests.
- Pour les requêtes qui nécessitent un ID (PUT, DELETE, GET par ID), assurez-vous d'utiliser un ID valide qui existe dans la base de données.
- Lors des tests de validation, utilisez intentionnellement des IDs invalides pour vérifier que la validation fonctionne correctement.
- Si vous modifiez le code des services, n'oubliez pas de les redémarrer pour que les changements prennent effet.
