package fr.snake.segments;

import fr.snake.FoodManager;
import fr.snake.Settings;
import fr.snake.snakes.Snake;

/**
 * Type de s egment par défaut, qui ne fait rien de p articulier.
 */
public class DefaultSegment extends Segment {
	public DefaultSegment(double x, double y, double angle) {
		super(x, y, angle) ;
	}

	@Override
	public void onCollision(Snake snake, Segment collidedSegment, Settings settings, FoodManager foodManager) {
		/**
		 *  Le srpent meurt. */
		snake.die(settings, foodManager) ;
	}
}
