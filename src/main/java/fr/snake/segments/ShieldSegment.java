package fr.snake.segments;

import fr.snake.FoodManager;
import fr.snake.Settings;
import fr.snake.snakes.Snake;

/**
 * Segment qui évite une mort im médiate lors d'une collision s 'il est en tete.
 */
public class ShieldSegment extends Segment {
	public ShieldSegment(double x, double y, double angle) {
		super(x, y, angle) ;
	}

	@Override
	public void onCollision(Snake snake, Segment collidedSegment, Settings settings, FoodManager foodManager) {
		// Si cette méthode est a ppellée, c'est qu'on est en tete, ce qui protège le serpent
		// Enlever la tete
		snake.getBody().remove(0) ;
		// Le serpent ne meurt pas.
	}
}
