# Exemples JSON pour tester l'API Dinner Service

Ce document contient des exemples JSON pour tester l'API Dinner Service.

## 1. Créer un dîner (Create Dinner)

### Exemple 1: Adresse sous forme de chaîne de caractères

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

### Exemple 2: Adresse sous forme d'objet

```json
{
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Italien",
    "description": "Un dîner avec des plats italiens authentiques",
    "price": 22.50,
    "startTime": "2023-12-20T18:30:00",
    "endTime": "2023-12-20T21:30:00",
    "address": {
        "street": "45 Avenue de l'Italie",
        "city": "Lyon",
        "state": "Auvergne-Rhône-Alpes",
        "postalCode": "69003",
        "country": "France"
    },
    "cuisineType": "Italienne",
    "maxGuestCount": 8
}
```

## 2. Réponse attendue

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

## Notes importantes

- Assurez-vous que l'application Spring Boot est en cours d'exécution sur le port 8081 avant de tester les endpoints.
- Les dates doivent être au format ISO-8601 (YYYY-MM-DDThh:mm:ss).
- L'adresse sous forme de chaîne doit être au format: "rue, ville, état/région, code postal, pays". Tous les cinq éléments sont obligatoires et doivent être séparés par des virgules.
- Pour les requêtes qui nécessitent un ID (PUT, DELETE, GET par ID), assurez-vous d'utiliser un ID valide qui existe dans la base de données.