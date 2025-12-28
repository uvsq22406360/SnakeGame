package fr.snake.segments;

import fr.snake.FoodManager;
import fr.snake.Settings;
import fr.snake.snakes.Snake;

import java.util.List;

/**
 * Segment qui au lieu de causer la mort du serpent qui rentre dedans casse la queue du s erpent à ce n iveau.
 */
public class WeakSegment extends Segment {
	public WeakSegment(double x, double y, double angle) {
		super(x, y, angle) ;
	}

	@Override
	public void onCollision(Snake snake, Segment collidedSegment, Settings settings, FoodManager foodManager) {
		// Si un segment faible est en tete, le serpent meurt quand meme.
		snake.die(settings, foodManager) ;
	}

	@Override
	public void onCollided(Snake snake, Segment collidedSegment, Settings settings, FoodManager foodManager) {
		// Couper le serpent à cet endroit- là
		List<Segment> body = snake.getBody() ;
		int index = body.indexOf(this) ;
		while (body.size() > index) body.remove(index) ;
	}

	@Override
	public boolean causesCollision() {
		return false;
	}

}
