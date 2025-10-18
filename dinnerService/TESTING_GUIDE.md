# Guide de Test Complet pour Dinner Service

Ce guide explique comment tester l'application Dinner Service de différentes manières, y compris les tests manuels avec Postman et les tests automatisés.

## 1. Tests Manuels avec Postman

### Configuration de base

- **URL de base**: `http://localhost:8081/api/dinners`
- **Content-Type**: `application/json`

### Test avec différents formats d'adresse

L'API accepte deux formats pour l'adresse:

#### Format 1: Adresse sous forme de chaîne de caractères

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
    "cuisineType": "Française"
}
```

#### Format 2: Adresse sous forme d'objet

```json
{
    "hostId": 1,
    "menuId": 1,
    "name": "Dîner Français",
    "description": "Un dîner avec des plats français traditionnels",
    "price": 25.99,
    "startTime": "2023-12-15T19:00:00",
    "endTime": "2023-12-15T22:00:00",
    "address": {
        "street": "123 Rue de Paris",
        "city": "Paris",
        "state": "Île-de-France",
        "postalCode": "75001",
        "country": "France"
    },
    "cuisineType": "Française"
}
```

### Test des transitions d'état des dîners

Les dîners peuvent avoir différents états:
- UPCOMING: Le dîner est à venir
- IN_PROGRESS: Le dîner est en cours
- COMPLETED: Le dîner est terminé
- CANCELLED: Le dîner a été annulé
- RESCHEDULED: Le dîner a été reprogrammé

Pour tester les transitions d'état:

1. **Créer un dîner**: L'état initial sera UPCOMING
2. **Supprimer un dîner**: Envoyer une requête DELETE à `/api/dinners/{id}` pour annuler un dîner (l'état devient CANCELLED)
3. **Mettre à jour un dîner avec de nouvelles dates**: L'état peut devenir RESCHEDULED
4. **Attendre que le dîner commence**: L'état devient automatiquement IN_PROGRESS lorsque la date de début est passée
5. **Attendre que le dîner se termine**: L'état devient automatiquement COMPLETED lorsque la date de fin est passée

Pour simuler le passage du temps sans attendre réellement, vous pouvez créer des dîners avec des dates de début et de fin dans le passé.

## 2. Tests Unitaires

Pour tester les composants individuels de l'application, vous pouvez créer des tests unitaires avec JUnit et Mockito.

### Exemple de test pour la classe Dinner

```java
import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.valueobject.DinnerStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class DinnerTest {

    @Test
    public void testCreateValidDinner() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(3);
        
        // Act
        Dinner dinner = new Dinner(
            null, 1L, 1L, "Test Dinner", "Description", 
            25.99, startTime, endTime, 
            "123 Test Street, Test City, Test State, 12345, Test Country", 
            "Test Cuisine"
        );
        
        // Assert
        assertTrue(dinner.isValid());
        assertEquals(DinnerStatus.UPCOMING, dinner.getStatus());
    }
    
    @Test
    public void testUpdateStatus() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        Dinner dinner = new Dinner(
            null, 1L, 1L, "Test Dinner", "Description", 
            25.99, startTime, endTime, 
            "123 Test Street, Test City, Test State, 12345, Test Country", 
            "Test Cuisine"
        );
        
        // Act
        dinner.updateStatus(LocalDateTime.now());
        
        // Assert
        assertEquals(DinnerStatus.IN_PROGRESS, dinner.getStatus());
    }
    
    @Test
    public void testCancelDinner() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(3);
        Dinner dinner = new Dinner(
            null, 1L, 1L, "Test Dinner", "Description", 
            25.99, startTime, endTime, 
            "123 Test Street, Test City, Test State, 12345, Test Country", 
            "Test Cuisine"
        );
        
        // Act
        boolean result = dinner.cancel();
        
        // Assert
        assertTrue(result);
        assertEquals(DinnerStatus.CANCELLED, dinner.getStatus());
    }
}
```

### Exemple de test pour DinnerApplicationServiceImpl

```java
import com.buberdinner.dinnerservice.application.dto.DinnerRequest;
import com.buberdinner.dinnerservice.application.dto.DinnerResponse;
import com.buberdinner.dinnerservice.application.service.impl.DinnerApplicationServiceImpl;
import com.buberdinner.dinnerservice.domain.entity.Dinner;
import com.buberdinner.dinnerservice.domain.repository.DinnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DinnerApplicationServiceImplTest {

    @Mock
    private DinnerRepository dinnerRepository;
    
    private DinnerApplicationServiceImpl dinnerService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dinnerService = new DinnerApplicationServiceImpl(dinnerRepository);
    }
    
    @Test
    public void testCreateDinner() {
        // Arrange
        DinnerRequest request = new DinnerRequest();
        request.setHostId(1L);
        request.setMenuId(1L);
        request.setName("Test Dinner");
        request.setDescription("Description");
        request.setPrice(25.99);
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3));
        request.setAddress("123 Test Street, Test City, Test State, 12345, Test Country");
        request.setCuisineType("Test Cuisine");
        
        Dinner savedDinner = new Dinner(
            1L, 1L, 1L, "Test Dinner", "Description", 
            25.99, request.getStartTime(), request.getEndTime(), 
            request.getAddress(), "Test Cuisine"
        );
        
        when(dinnerRepository.save(any(Dinner.class))).thenReturn(savedDinner);
        
        // Act
        DinnerResponse response = dinnerService.createDinner(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Dinner", response.getName());
        assertEquals("UPCOMING", response.getStatus());
        verify(dinnerRepository, times(1)).save(any(Dinner.class));
    }
}
```

## 3. Tests d'Intégration

Les tests d'intégration vérifient que les différents composants de l'application fonctionnent correctement ensemble.

### Exemple de test d'intégration pour le contrôleur

```java
import com.buberdinner.dinnerservice.presentation.dto.DinnerRequest;
import com.buberdinner.dinnerservice.presentation.dto.AddressDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class DinnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testCreateDinnerWithStringAddress() throws Exception {
        // Arrange
        DinnerRequest request = new DinnerRequest();
        request.setHostId(1L);
        request.setMenuId(1L);
        request.setName("Test Dinner");
        request.setDescription("Description");
        request.setPrice(25.99);
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3));
        request.setAddress("123 Test Street, Test City, Test State, 12345, Test Country");
        request.setCuisineType("Test Cuisine");
        
        // Act & Assert
        mockMvc.perform(post("/api/dinners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Dinner"))
                .andExpect(jsonPath("$.status").value("UPCOMING"));
    }
    
    @Test
    public void testCreateDinnerWithAddressObject() throws Exception {
        // Arrange
        DinnerRequest request = new DinnerRequest();
        request.setHostId(1L);
        request.setMenuId(1L);
        request.setName("Test Dinner");
        request.setDescription("Description");
        request.setPrice(25.99);
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(3));
        
        // Create address object
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet("123 Test Street");
        addressDto.setCity("Test City");
        addressDto.setState("Test State");
        addressDto.setPostalCode("12345");
        addressDto.setCountry("Test Country");
        request.setAddressDto(addressDto);
        
        request.setCuisineType("Test Cuisine");
        
        // Act & Assert
        mockMvc.perform(post("/api/dinners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Dinner"))
                .andExpect(jsonPath("$.status").value("UPCOMING"));
    }
}
```

## 4. Comment exécuter les tests

### Tests unitaires et d'intégration

Pour exécuter les tests automatisés, vous pouvez utiliser Maven:

```bash
# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=DinnerTest

# Exécuter les tests d'une classe spécifique
mvn test -Dtest=DinnerControllerIntegrationTest
```

### Tests manuels avec Postman

1. Assurez-vous que l'application est en cours d'exécution:
   ```bash
   mvn spring-boot:run
   ```

2. Ouvrez Postman et suivez les instructions du guide POSTMAN_TESTING_GUIDE.md pour tester les différents endpoints.

3. Pour tester avec un objet d'adresse, utilisez le format JSON suivant:
   ```json
   {
       "hostId": 1,
       "menuId": 1,
       "name": "Dîner Français",
       "description": "Un dîner avec des plats français traditionnels",
       "price": 25.99,
       "startTime": "2023-12-15T19:00:00",
       "endTime": "2023-12-15T22:00:00",
       "address": {
           "street": "123 Rue de Paris",
           "city": "Paris",
           "state": "Île-de-France",
           "postalCode": "75001",
           "country": "France"
       },
       "cuisineType": "Française"
   }
   ```

## 5. Conseils pour le débogage

- Vérifiez les logs de l'application pour voir les erreurs
- Utilisez les outils de débogage de votre IDE pour suivre l'exécution du code
- Vérifiez les réponses HTTP dans Postman pour comprendre les erreurs
- Assurez-vous que le format de l'adresse est correct (soit une chaîne au format attendu, soit un objet avec tous les champs requis)
- Vérifiez que les dates sont au format ISO-8601 (YYYY-MM-DDThh:mm:ss)