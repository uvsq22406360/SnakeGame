package fr.snake.states;

import fr.snake.*;
import fr.snake.segments.Segment;
import fr.snake.snakes.Snake;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Un état re présentant une partie de jeu en cours.
 */
public class GameState extends State {
	protected Settings settings ;
	protected final Background background ;
	protected final FoodManager foodManager ;

	/**
	 * Tous les se rpents présents sur la carte du jeu.
	 */
	protected List<Snake> snakes ;
	protected int cameraX, cameraY ;

	public GameState(Settings settings) {
		background = new Background(settings.getDefaultMap()) ;
		this.settings = settings;

		snakes = new ArrayList<>() ;
		foodManager = FoodManager.createFoodManager(settings) ;
	}

	@Override
	public void update() {
		// Mettre à jour les serpents et comp iler une liste des serpents morts (car on ne peut pas les retirer de la liste
		// en meme temps qu'on parcourt la liste, sous peine d'une Concurrent Modification Exception)
		ArrayList<Snake> deadSnakes = new ArrayList<>() ;
		for (Snake snake : snakes) {
			if (snake.isAlive()) {
				snake.updateAi(settings, foodManager, snakes) ;
				snake.checkCollisions(settings, snakes, foodManager) ;
				snake.eat(settings, foodManager) ;
			} else {
				deadSnakes.add(snake) ;
			}
		}
		for (Snake deadSnake : deadSnakes) {
			removeSnake(deadSnake.getUuid()) ;
		}

		if (foodManager.getFoods().length < settings.getMinimumFoods()) {
			foodManager.createFood(settings) ;
		}

		if (foodManager.getFoods().length > settings.getMaximumFoods()) {
			foodManager.removeFood(foodManager.getFoods()[0]) ;
		}

		if (snakes.size() < settings.getMinimumSnakes()) {
			// Rajouter un serpent IA
			addSnake(Snake.createAISnake(settings)) ;
		}
	}

	@Override
	public void paint(Graphics g) {
		// Sur toutes les ve rsions de Java assez récentes, il est garanti que l'argument Graphics que reçoit
		// la fonction paint (Graphics) est en fait un Graphics 2D.
		Graphics2D g2d = (Graphics2D) g;

		// On dessine dans l'ordre l'arrière-plan, le(s) serpent(s) et la nourriture.
		background.paint(g2d, settings, cameraX, cameraY) ;
		for (Snake snake : snakes) {
			snake.paint(g2d, settings, cameraX, cameraY) ;
		}
		foodManager.paint(g2d, settings, cameraX, cameraY) ;
	}

	/**
	 * Centrer la caméra sur le serpent.
	 * @param snake serpent sur lequel centrer
	 */
	public void centerCameraOn(Snake snake) {
		Segment head = snake.getBody().get(0) ;
		double headX = head.getX() ;
		double headY = head.getY() ;

		double x = headX - settings.getWindowWidth() / 2 ;
		double y = headY - settings.getWindowHeight() / 2 ;
		cameraX = (int) x ;
		cameraY = (int) y ;
	}

	/**
	 * Rajoute un serpent au jeu.
	 * @param snake le serpent à r ajouter
	 */
	public void addSnake(Snake snake) {
		snakes.add(snake) ;
	}

	/**
	 * Retire un serpent du jeu.
	 * @param uuid l'UUID du serpent
	 */
	public void removeSnake(UUID uuid) {
		Optional<Snake> maybeSnake = getSnakeByUuid(uuid) ;
		if (maybeSnake.isPresent()) {
			Snake snake = maybeSnake.get() ;
			snakes.remove(snake) ;
		}
	}

	/**
	 * R envoie un tableau immuable des serpents présents dans le jeu.
	 * Cette méthode renvoie un tableau plutot qu'une List<Snake> afin de f orcer
	 * par le typage le fait de devoir utiliser les méthodes <code> addSnake </code> et <code> removeSnake () </code>
	 * @return tableau des s erpents
	 */
	public Snake[] getSnakes() {
		return snakes.toArray(new Snake[0]) ;
	}

	public FoodManager getFoodManager() {
		return foodManager ;
	}

	public Food[] getFoods() {
		return foodManager.getFoods() ;
	}

	/**
	 * Cherche un serpent qui a l'UUID donné. S'il n'y en un pas, la méthode renvoie un Optional vide.
	 * @param uuid L'UUID du serpent à chercher
	 * @return optionel contenant le serpent ou pas
	 */
	public Optional<Snake> getSnakeByUuid(UUID uuid) {
		for (Snake snake : snakes) {
			if (snake.getUuid().equals(uuid)) {
				return Optional.of(snake) ;
			}
		}
		return Optional.empty() ;
	}

	/**
	 * Prends un serpent existant et change ses pr opriétés.
	 * @param snake serpent existant
	 */
	public void changeSnake(Snake snake) {
		Optional<Snake> maybeOldSnake = getSnakeByUuid(snake.getUuid()) ;
		if (maybeOldSnake.isPresent()) {
			Snake oldSnake = maybeOldSnake.get() ;
			if (oldSnake.isAlive() && !snake.isAlive()) {
				// Il faut faire mourir le serpent
				oldSnake.die(settings, foodManager) ;
			}
			oldSnake.copy(snake) ;
		} else {
			throw new IllegalStateException("Impossible de changer un serpent qui n'existe pas") ;
		}
	}
}
