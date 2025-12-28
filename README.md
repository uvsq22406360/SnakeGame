# Snake
Ce projet utilise Maven.

## Compilation
```shell
mvn compile
```

## Exécution
```shell
---executer le serveur puis le application 
mvn compile exec:java -Dexec.mainClass="fr.snake.multiplayer.Server"
mvn compile exec:java -Dexec.mainClass="fr.snake.App"
```

## Test
```shell
mvn compile exec:java -Dexec.mainClass="fr.snake.multiplayer.Server"
```

## Fonctionnalités
- [x] Déplacement du serpent
- [x] Joueur automatique / IA
- [x] Gestion de la mort
- [x] Gestion de la croissance
- [x] Déplacement fluide
- [x] Déplacement libre
  - [x] Souris
  - [x] Clavier
- [x] Terrain sans bords
- [x] Mode réseau classique
- [x] Monde permanent
- [x] Règles slither.io

## Guide
La nourriture rouge rajoute juste des segments au serpent en fonction de sa taille.
La nourriture blanche et grise rajoute un segment faible ou bouclier respectivement.
La nourriture verte est empoisonnée et vous tuera.
La nourriture bleue augmente votre vitesse.

Un segment faible est un segment qui lorsqu'un serpent se heurte contre lui, va couper la queue du serpent à ce niveau.
Un segment bouclier est un segment qui, lorsqu'il est placé en tete, protège le serpent si jamais il entre en collision avec un autre serpent.

