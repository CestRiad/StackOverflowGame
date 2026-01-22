StackOverflowGame — Moteur de jeu 2D avec LibGDX

Projet réalisé dans le cadre du projet noté de PCOO
Université Nice Côte d’Azur

⸻

Compilation & Exécution

Depuis la racine du projet :
./gradlew lwjgl3:run

-> Le moteur se lance automatiquement avec la configuration graphique adaptée.

⸻

Présentation du projet

StackOverflowGame est un moteur de jeu 2D développé en Java avec la bibliothèque LibGDX.
Il s’agit d’un moteur de type Arcade / Block Breaker, entièrement piloté par des fichiers de configuration Tiled.

Objectif principal
Permettre l’enrichissement du jeu (niveaux, blocs, entités, propriétés des entités…) sans modifier le code Java, uniquement via Tiled.

⸻

Fonctionnalités principales
	-	Chargement dynamique des niveaux depuis des fichiers .tmx (Tiled)
	-	Création automatique des entités à partir de Tiled :
	    - Ball
	    - Bumper
	    - Blocks
	-	Propriétés personnalisables dans Tiled :
		- color
	    - hp
		- destroyable
		- scoreValue
		- displayWidth
		- displayHeight
    -   Système de collisions
    -   Système de score
	-   Écran d’introduction avec transition animée
	-   Gestion complète du jeu sans recompilation lors de l’ajout de niveaux

⸻

Enrichissement via Tiled (sans Java)

L’ajout ou la modification du contenu du jeu se fait exclusivement via Tiled:
	-	Ajouter un nouveau niveau -> ajouter un fichier .tmx
	-	Modifier les blocs -> changer leurs propriétés dans Tiled
	-	Changer la disposition -> déplacer les objets dans la carte

-> Aucune modification du code Java n’est nécessaire.

⸻

Architecture & Concepts POO

Le projet met en oeuvre les concepts fondamentaux de la PCOO:

	Héritage :
	    -	Entity (classe abstraite)
        - 	Ball, Block, Bumper
        
	Polymorphisme:
	    -	Méthodes communes (render, applyTiledProperties)
        
	Encapsulation:
	    -	Attributs privés, accès contrôlé
        
	Pattern utilisé:
	    -	EntityFactory pour instancier les entités depuis Tiled
        
	Séparation logique plus ou moins proche du MVC:
	    -	Modèle : entités
	    -	Vue : rendu LibGDX
	    -	Contrôleur : GameScreen

⸻

Organisation du projet

StackOverflowGame/
├── core/
│   ├── assets/             # Ressources (maps, tilesets, textures, fonts)
│   └── src/main/java/
│       └── com/stackoverflowgame/
│           ├── entities/   # Ball, Block, Bumper, Entity
│           ├── utils/      # EntityFactory, CollisionUtils
│           ├── GameScreen.java
│           └── StackOverflowGame.java
├── lwjgl3/
│   └── src/main/java/
│       └── com/stackoverflowgame/lwjgl3/
│           ├── Lwjgl3Launcher.java
│           └── StartupHelper.java
└── gradlew

Prérequis
	-	JDK 17 (Java)
	-	Gradle
	-	macOS / Windows / Linux
	-	Tiled (pour modifier les niveaux)

⸻

Répartition du travail

Projet réalisé par :
	-	Riad Hechaichi
	-	Dan Toubalem
	-	Samy Yadi

(Les détails de la contribution de chaque membre sont décrits dans le rapport en .pdf)

⸻

Dépôt Git

Le lien vers le dépôt est fourni dans le rapport et ce README.
LIEN: https://github.com/CestRiad/StackOverflowGame
