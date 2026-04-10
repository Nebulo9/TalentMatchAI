# TalentMatchAI
TalentMatch AI est une plateforme conçue pour faciliter la rencontre entre les talents techniques et les besoins des entreprises. Elle permet d'automatiser la création de profils à partir de données réelles (GitHub) et de fournir une analyse intelligente de l'adéquation entre un candidat et une offre d'emploi.

---

## Équipe

Projet réalisé en trio dans le cadre du cours de Programmation par Composants (M1 Cybersécurité) :

- **Daoud KARAM** — [@daoudkaram](https://github.com/daoudkaram)
- **Elias TURKI** — [@eliastrk](https://github.com/eliastrk)
- **Mathéo DESSAUVAGES** — [@nebulo9](https://github.com/nebulo9)

---

## Stack technique

- **Java 21** + **Spring Boot 4.0.3**
- **Spring Data JPA** + **H2** (base de données en mémoire)
- **Spring Security** (Basic Auth)
- **Spring Kafka** + **Redpanda** (broker Kafka-compatible)
- **Ollama** (modèle `llama3.2:1b` pour l'analyse IA)
- **GitHub API** (import de profils)
- **Spring Actuator** (observabilité)

---

## Prérequis

- Java 21+
- Maven 3.9+
- Docker

---

## Démarrage de l'infrastructure

Le script `environment_setup.sh` démarre les conteneurs Ollama et Redpanda, télécharge le modèle IA et crée le topic Kafka :

```bash
chmod +x environment_setup.sh
./environment_setup.sh
```

Cela effectue automatiquement :
- Création/démarrage du conteneur **Ollama** sur le port `11434`
- Téléchargement du modèle `llama3.2:1b`
- Création/démarrage du conteneur **Redpanda** sur le port `9092`
- Création du topic `matching-requests` (3 partitions, 1 réplica)

---

## Lancement de l'application

```bash
mvn spring-boot:run
```

L'application démarre sur le port `8080`.

La console H2 est accessible à l'adresse : [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL : `jdbc:h2:mem:talentmatchdb`
- Utilisateur : `sa`
- Mot de passe : *(vide)*

---

## Exécution des tests

```bash
mvn test
```

Les tests ne nécessitent pas Kafka ni Ollama (ils sont mockés).

- **28 tests** au total :
  - 15 tests unitaires (services avec Mockito)
  - 8 tests web (contrôleurs avec MockMvc)
  - 5 tests d'intégration (scénarios complets avec H2)

---

## Sécurité

Deux endpoints nécessitent une authentification **Basic Auth** :
- `POST /api/candidates/import`
- `POST /api/matching/analyze`

| Utilisateur  | Mot de passe    | Rôle      |
|--------------|-----------------|-----------|
| `admin`      | `admin123`      | `ADMIN`   |
| `recruiter`  | `recruiter123`  | `RECRUITER` |

---

## Endpoints API

### Candidats — `/api/candidates`

**Créer un candidat**
```bash
curl -s -X POST http://localhost:8080/api/candidates \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Alice",
    "lastName": "Dupont",
    "email": "alice.dupont@example.com",
    "githubUsername": "alice-dev",
    "skills": ["Java", "Spring Boot", "PostgreSQL"],
    "yearsOfExperience": 3,
    "bio": "Développeuse backend passionnée par les microservices"
  }' | jq .
```
→ `201 Created`

**Lister tous les candidats**
```bash
curl -s http://localhost:8080/api/candidates | jq .
```
→ `200 OK`

**Obtenir un candidat par ID**
```bash
curl -s http://localhost:8080/api/candidates/{id} | jq .
```
→ `200 OK` ou `404 Not Found`

**Mettre à jour un candidat**
```bash
curl -s -X PUT http://localhost:8080/api/candidates/{id} \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Alice",
    "lastName": "Dupont",
    "email": "alice.dupont@example.com",
    "skills": ["Java", "Spring Boot", "Kafka"],
    "yearsOfExperience": 4
  }' | jq .
```
→ `200 OK`

**Supprimer un candidat**
```bash
curl -s -X DELETE http://localhost:8080/api/candidates/{id}
```
→ `204 No Content`

**Importer un profil depuis GitHub** *(authentification requise)*
```bash
curl -s -u admin:admin123 -X POST \
  "http://localhost:8080/api/candidates/import?githubUsername=torvalds" | jq .
```
→ `201 Created` — extrait les langages, l'expérience et la bio depuis GitHub

---

### Offres d'emploi — `/api/job-offers`

**Créer une offre**
```bash
curl -s -X POST http://localhost:8080/api/job-offers \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Développeur Backend Java/Spring",
    "company": "TechCorp",
    "requiredSkills": ["Java", "Spring Boot", "PostgreSQL", "Docker"],
    "description": "Nous recherchons un développeur backend expérimenté.",
    "location": "Paris",
    "salaryRange": "45k-55k"
  }' | jq .
```
→ `201 Created`

**Lister toutes les offres**
```bash
curl -s http://localhost:8080/api/job-offers | jq .
```
→ `200 OK`

**Obtenir une offre par ID**
```bash
curl -s http://localhost:8080/api/job-offers/{id} | jq .
```
→ `200 OK` ou `404 Not Found`

**Mettre à jour une offre**
```bash
curl -s -X PUT http://localhost:8080/api/job-offers/{id} \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Lead Backend Java",
    "company": "TechCorp",
    "requiredSkills": ["Java", "Spring Boot", "Kafka"],
    "description": "Poste de lead technique.",
    "location": "Remote"
  }' | jq .
```
→ `200 OK`

**Supprimer une offre**
```bash
curl -s -X DELETE http://localhost:8080/api/job-offers/{id}
```
→ `204 No Content`

---

### Matching — `/api/matching`

**Lancer une analyse IA** *(authentification requise)*
```bash
curl -s -u admin:admin123 -X POST http://localhost:8080/api/matching/analyze \
  -H 'Content-Type: application/json' \
  -d '{
    "candidateId": "550e8400-e29b-41d4-a716-446655440000",
    "jobOfferId": "660e8400-e29b-41d4-a716-446655440001"
  }' | jq .
```
→ `202 Accepted` avec statut `PENDING` — le traitement est asynchrone via Kafka

**Script complet de test bout-en-bout**
```bash
BASE_URL="http://localhost:8080"

CANDIDATE_ID=$(curl -s -X POST $BASE_URL/api/candidates \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Alice","lastName":"Dupont","email":"alice@example.com","skills":["Java","Spring Boot"],"yearsOfExperience":3}' \
  | jq -r '.id')

JOB_ID=$(curl -s -X POST $BASE_URL/api/job-offers \
  -H 'Content-Type: application/json' \
  -d '{"title":"Backend Developer","company":"Acme","requiredSkills":["Java"],"description":"Great role","location":"Paris"}' \
  | jq -r '.id')

MATCHING_ID=$(curl -s -u admin:admin123 -X POST $BASE_URL/api/matching/analyze \
  -H 'Content-Type: application/json' \
  -d "{\"candidateId\":\"$CANDIDATE_ID\",\"jobOfferId\":\"$JOB_ID\"}" \
  | jq -r '.id')

while true; do
  RESULT=$(curl -s $BASE_URL/api/matching/results/$MATCHING_ID)
  STATUS=$(echo $RESULT | jq -r '.status')
  echo "Status: $STATUS"
  if [[ "$STATUS" == "COMPLETED" || "$STATUS" == "FAILED" ]]; then
    echo $RESULT | jq .
    break
  fi
  sleep 3
done
```

**Exemple de résultat COMPLETED**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "candidateId": "550e8400-e29b-41d4-a716-446655440000",
  "jobOfferId": "660e8400-e29b-41d4-a716-446655440001",
  "status": "COMPLETED",
  "score": 78,
  "analysis": "Points forts :\n- Maîtrise solide de Java et Spring Boot\n\nAxes d'amélioration :\n- Docker : compétence à développer\n\nRecommandation : Candidat prometteur.",
  "requestedAt": "2024-03-15T15:00:00Z",
  "completedAt": "2024-03-15T15:00:12Z",
  "errorMessage": null
}
```

**Lister tous les résultats**
```bash
curl -s http://localhost:8080/api/matching/results | jq .
```

**Obtenir un résultat par ID**
```bash
curl -s http://localhost:8080/api/matching/results/{id} | jq .
```

**Résultats par candidat**
```bash
curl -s http://localhost:8080/api/matching/candidate/{candidateId} | jq .
```

**Résultats par offre**
```bash
curl -s http://localhost:8080/api/matching/job/{jobOfferId} | jq .
```

---

## Gestion des erreurs

Toutes les erreurs retournent une réponse structurée :

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Erreur de validation des données",
  "details": {
    "email": "Email invalide",
    "skills": "Au moins une compétence est requise"
  },
  "timestamp": "2024-03-15T10:30:00Z"
}
```

| Code HTTP | Code erreur           | Cause                              |
|-----------|-----------------------|------------------------------------|
| 400       | `VALIDATION_ERROR`    | Données invalides                  |
| 404       | `NOT_FOUND`           | Ressource inexistante              |
| 502       | `GITHUB_IMPORT_ERROR` | Erreur API GitHub                  |
| 503       | `AI_SERVICE_ERROR`    | Service Ollama indisponible        |
| 500       | `INTERNAL_ERROR`      | Erreur interne                     |

---

## Observabilité (Actuator)

**Santé globale** (inclut les indicateurs Kafka et Ollama) :
```bash
curl -s http://localhost:8080/actuator/health | jq .
```

**Statistiques de matching** (endpoint personnalisé) :
```bash
curl -s http://localhost:8080/actuator/matching-stats | jq .
```
```json
{
  "total": 5,
  "pending": 1,
  "processing": 0,
  "completed": 3,
  "failed": 1
}
```

**Métriques** :
```bash
curl -s http://localhost:8080/actuator/metrics | jq .
```

---

## Architecture

L'application suit une architecture 3-tiers stricte :

```
Controller  →  Service  →  Repository  →  H2
                  ↓
            Kafka Producer
                  ↓ (async)
            Kafka Consumer
                  ↓
            Ollama (IA)
```

**Flux de matching asynchrone :**
1. `POST /api/matching/analyze` reçu par le contrôleur
2. Le service crée un `MatchingResult` en statut `PENDING`
3. Le producer publie un message sur le topic `matching-requests`
4. Réponse immédiate `202 Accepted` au client
5. Le consumer Kafka lit le message
6. Le consumer charge le candidat et l'offre depuis la base
7. Le consumer appelle Ollama avec un prompt structuré
8. Le `MatchingResult` est mis à jour : `COMPLETED` (avec score et analyse) ou `FAILED` (avec message d'erreur)

---

## Choix techniques et difficultés rencontrées

- **Spring Boot 4.0.3** introduit des changements majeurs par rapport à Boot 3.x : migration vers Jackson 3.x (`tools.jackson.*`), déplacement des API de santé vers `spring-boot-health`, suppression de `@MockBean` au profit de `@MockitoBean`, et absence d'auto-configuration Kafka (beans `KafkaTemplate`, `ConsumerFactory`, `KafkaAdmin`, `ConcurrentKafkaListenerContainerFactory` à déclarer explicitement avec `@EnableKafka`).

- **Lazy loading JPA** : les `@ElementCollection` (skills, requiredSkills) sont chargées en `EAGER` pour éviter les `LazyInitializationException` lorsque le consumer Kafka accède aux entités hors session.

- **`MatchingResult` sans relation JPA** : les IDs `candidateId` et `jobOfferId` sont stockés directement (sans `@ManyToOne`) pour découpler le traitement asynchrone et éviter les problèmes de session Hibernate dans le consumer.

- **Sécurité** : utilisation de `PasswordEncoderFactories.createDelegatingPasswordEncoder()` (à la place de `BCryptPasswordEncoder` seul) pour supporter le préfixe `{noop}` dans `CustomUserDetailsService`.
