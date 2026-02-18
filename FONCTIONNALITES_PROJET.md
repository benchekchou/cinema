# Documentation des fonctionnalités - Projet Cinema

## 1. Objectif du document
Ce document décrit :
- les fonctionnalités qui existent déjà dans le code actuel ;
- les limites observées ;
- ce qu'il faut ajouter pour obtenir une application de gestion de cinéma complète.

## 2. Fonctionnalités existantes

### 2.1 Stack technique
- Java 17
- Spring Boot 3.4.2
- Spring Data JPA
- Spring Data REST
- H2 (runtime)
- Lombok

### 2.2 Modèle métier (entités)
Le projet contient les entités suivantes :
- `Ville` : nom, longitude, latitude.
- `Cinema` : nom, longitude, attitude, nombre de salles, lien vers `Ville`.
- `Salle` : nom, nombre de places, lien vers `Cinema`.
- `Place` : numéro, coordonnées, lien vers `Salle`.
- `Seance` : heure de début.
- `Categorie` : nom.
- `Film` : titre, description, durée, réalisateur, photo, date de sortie, lien vers `Categorie`.
- `ProjectionFilm` : date de projection, prix, liens vers `Film`, `Salle`, `Seance`.
- `Ticket` : nom client, prix, code paiement, statut réservé, liens vers `Place` et `ProjectionFilm`.

### 2.3 Persistance et CRUD
- Chaque entité possède un repository `JpaRepository`.
- Le CRUD standard est donc disponible pour chaque agrégat.
- Spring Data REST est présent, donc les repositories sont exposés automatiquement en REST.

### 2.4 Initialisation automatique des données
Au démarrage, l'application exécute un `CommandLineRunner` qui appelle un service d'initialisation :
- création des villes ;
- création des cinémas ;
- création des salles ;
- création des places ;
- création des séances ;
- création des catégories ;
- création des films ;
- création des projections ;
- création des tickets.

### 2.5 API REST exposée
Avec Spring Data REST, les endpoints de base sont exposés automatiquement (exemples attendus) :
- `/villes`
- `/cinemas`
- `/salles`
- `/places`
- `/seances`
- `/categories`
- `/films`
- `/projectionFilms`
- `/tickets`

### 2.6 Tests existants
- Un test de chargement de contexte existe : `contextLoads()`.
- Il n'y a pas encore de tests métier, d'intégration API, ni de tests de réservation.

## 3. Limites et écarts observés

### 3.1 Point critique actuel
Dans `CenimaApplication`, l'ordre des appels d'initialisation est incorrect :
- `initCinema()` est appelé avant `initVilles()`.
- `initSalles()` est appelé alors qu'aucun cinéma n'a été créé.
- `initProjectionFilms()` est appelé avant `initFilms()` et avant la création utile des salles/cinémas.

Conséquence :
- la base n'est pas alimentée avec un graphe complet cohérent ;
- seules certaines données sont réellement créées (villes, catégories, séances, films), tandis que les cinémas/salles/projections/tickets peuvent rester vides.

### 3.2 Limites fonctionnelles
- Pas de workflow métier complet de réservation (choix place, verrouillage, confirmation, paiement, ticket final).
- Pas d'authentification/autorisation (admin/client).
- Pas de validation stricte (ex: unicité code paiement, contraintes métier).
- Pas de gestion d'erreurs centralisée (`@ControllerAdvice`).
- Pas de documentation d'API (Swagger/OpenAPI).
- Pas de configuration avancée pour la base ni de migrations versionnées (Flyway/Liquibase).

## 4. Ce qu'il faut ajouter (priorités)

### Priorité 1 (indispensable)
- Corriger l'ordre d'initialisation dans `run()` :
  `initVilles()` -> `initCinema()` -> `initSalles()` -> `initPlaces()` -> `initSeances()` -> `initCategories()` -> `initFilms()` -> `initProjectionFilms()` -> `initTickets()`
- Ajouter un endpoint pour réserver une place.
- Vérifier qu'un ticket n'est pas déjà réservé.
- Générer et valider un code de paiement.
- Garantir une mise à jour atomique via transaction.
- Ajouter des validations sur les DTO/entités (`@NotNull`, `@Size`, etc.).
- Ajouter des tests unitaires et d'intégration sur le flux de réservation.

### Priorité 2 (fortement recommandé)
- Ajouter une recherche des films par catégorie.
- Ajouter une recherche des projections par ville/cinéma/date.
- Ajouter une recherche des tickets disponibles par projection.
- Introduire des DTO + mapping pour ne pas exposer directement les entités.
- Ajouter une gestion globale des erreurs (codes HTTP clairs + messages utiles).
- Documenter l'API avec Swagger/OpenAPI.

### Priorité 3 (production)
- Ajouter Spring Security + JWT (rôles `ADMIN` / `CLIENT`).
- Ajouter migrations Flyway/Liquibase.
- Préparer un profil `prod` (PostgreSQL/MySQL, variables d'environnement).
- Ajouter logs structurés, métriques et health checks (`Spring Boot Actuator`).

## 5. Résultat attendu après ajouts
Après ces ajouts, le projet passera :
- d'un socle CRUD technique auto-exposé,
- à une application métier exploitable avec réservation fiable, sécurité, tests et API documentée.
