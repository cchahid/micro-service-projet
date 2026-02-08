# Résumé des Fonctionnalités des Services Buber Dinner

Ce document présente un résumé des fonctionnalités des différents microservices qui composent l'application Buber Dinner.

## 1. Service Utilisateur (User Service)

Le Service Utilisateur gère l'authentification, l'enregistrement et la gestion des utilisateurs.

### Fonctionnalités principales

- **Gestion des utilisateurs** : Création, récupération et validation des utilisateurs
- **Authentification** : Connexion des utilisateurs et génération de tokens JWT
- **Gestion des rôles** : Support des rôles HOST (hôte) et GUEST (invité)

### Endpoints clés

- **POST /api/auth/signUp/Host** : Inscription d'un nouvel utilisateur avec le rôle HOST
- **POST /api/auth/signUp/Guest** : Inscription d'un nouvel utilisateur avec le rôle GUEST
- **POST /api/auth/login** : Authentification d'un utilisateur et génération d'un token JWT
- **GET /api/users/{id}/exists** : Vérification de l'existence d'un utilisateur
- **GET /api/users/{id}/isHost** : Vérification si un utilisateur a le rôle HOST
- **GET /api/users/{id}** : Récupération des détails d'un utilisateur

### Modèle de données

- **User** : Entité principale avec les attributs id, nom, prenom, email, password et role
- **Role** : Énumération avec les valeurs HOST et GUEST

### Événements publiés

- **HostCreated** : Publié lorsqu'un nouvel hôte est créé
- **GuestCreated** : Publié lorsqu'un nouvel invité est créé

## 2. Service Dîner (Dinner Service)

Le Service Dîner gère la création, la mise à jour, la suppression et la récupération des dîners.

### Fonctionnalités principales

- **Gestion des dîners** : Création, mise à jour, suppression et récupération des dîners
- **Validation des hôtes** : Vérification que l'hôte d'un dîner existe et a le rôle HOST
- **Validation des menus** : Vérification que le menu d'un dîner existe (fonctionnalité future)
- **Gestion du statut des dîners** : Mise à jour automatique du statut des dîners en fonction de la date et de l'heure

### Endpoints clés

- **POST /api/dinners** : Création d'un nouveau dîner
- **PUT /api/dinners/{id}** : Mise à jour d'un dîner existant
- **DELETE /api/dinners/{id}** : Suppression (annulation) d'un dîner
- **GET /api/dinners/{id}** : Récupération des détails d'un dîner
- **GET /api/dinners** : Récupération de tous les dîners
- **GET /api/dinners/host/{hostId}** : Récupération des dîners d'un hôte spécifique
- **GET /api/dinners/menu/{menuId}** : Récupération des dîners utilisant un menu spécifique

### Modèle de données

- **Dinner** : Entité principale avec les attributs id, hostId, menuId, name, description, price, timeRange, address, cuisineType, maxGuestCount et status
- **DinnerStatus** : Énumération avec les valeurs UPCOMING, IN_PROGRESS, COMPLETED, CANCELLED et RESCHEDULED

## 3. Communication entre les Services

Les services communiquent entre eux via des appels REST API.

### User Service → Dinner Service

- Le Dinner Service utilise le User Service pour valider que l'hôte d'un dîner existe et a le rôle HOST avant de créer ou de mettre à jour un dîner.
- Le Dinner Service utilise la classe `UserServiceClient` pour faire des appels REST au User Service.

### Menu Service → Dinner Service (Implémentation future)

- Le Dinner Service est préparé pour utiliser un futur Menu Service pour valider que le menu d'un dîner existe avant de créer ou de mettre à jour un dîner.
- Le Dinner Service utilise la classe `MenuServiceClient` (actuellement un placeholder) pour faire des appels REST au futur Menu Service.

### Configuration

Les URLs des services sont configurées dans les fichiers `application.properties` :

```properties
# Dans dinnerService/src/main/resources/application.properties
service.user.url=http://localhost:8080
service.menu.url=http://localhost:8082
```

## 4. Améliorations futures

1. Implémentation du Menu Service et complétion de la classe `MenuServiceClient`
2. Ajout de circuit breakers pour gérer les indisponibilités de service de manière élégante
3. Implémentation de la découverte de service pour une résolution dynamique des services
4. Ajout d'authentification et d'autorisation pour la communication inter-services
5. Implémentation d'une passerelle API (API Gateway) pour centraliser les requêtes
6. Mise en place d'un service de configuration centralisé