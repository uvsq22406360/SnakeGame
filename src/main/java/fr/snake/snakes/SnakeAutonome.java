package fr.snake.snakes;

import fr.snake.Food;
import fr.snake.FoodManager;
import fr.snake.Settings;
import fr.snake.Skin;
import fr.snake.segments.Segment;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Un serpent contrôlé par intelligence artificielle
 */
public class SnakeAutonome extends Snake {
	private transient Random r = new Random() ;

	protected SnakeAutonome(UUID uuid, int startX, int startY, int taille, int bodyWidth, Skin skin) {
		super(uuid, startX, startY, taille, bodyWidth, skin) ;
	}

	public Optional<Food> getClosestFood(Segment head, FoodManager foodManager) {
		double distance_min = Double.POSITIVE_INFINITY;
		Food closest = null;
		for (Food f : foodManager.getFoods()) {
			if (f.getType() != Food.Type.POISONED) {
				double distance = (f.getX() - head.getX()) * (f.getX() - head.getX())
						+ (f.getY() - head.getY()) * (f.getY() - head.getY()) ;
				if (distance < distance_min) {
					distance_min = distance ;
					closest = f;
				}
			}
		}
		return Optional.ofNullable(closest) ;
	}

	public Optional<Point2D> getClosestThreat(Segment head, FoodManager foodManager, List<Snake> snakes) {
		double distance_min = Double.POSITIVE_INFINITY ;
		Point2D closest = null ;

		for (Food f : foodManager.getFoods()) {
			if (f.getType() == Food.Type.POISONED) {
				double distance = (f.getX() - head.getX()) * (f.getX() - head.getX())
						+ (f.getY() - head.getY()) * (f.getY() - head.getY()) ;
				if (distance < distance_min) {
					distance_min = distance ;
					closest = new Point2D.Double(f.getX() + f.getSize(), f.getY() + f.getSize()) ;
				}
			}
		}

		for (Snake snake : snakes) {
			if (snake != this) {
				for (Segment segment : snake.getBody()) {
					if (segment.causesCollision()) {
						double distance = (segment.getX() - head.getX()) * (segment.getX() - head.getX())
								+ (segment.getY() - head.getY()) * (segment.getY() - head.getY()) ;
						if (distance < distance_min) {
							distance_min = distance;
							closest = new Point2D.Double(segment.getX() + snake.getBodyWidth() / 2, segment.getY() + snake.getBodyWidth() / 2) ;
						}
					}
				}
			}
		}

		return Optional.ofNullable(closest) ;
	}

	@Override
	public void updateAi(Settings settings, FoodManager foodManager, List<Snake> snakes) {
		Segment head = this.getBody().get(0) ;
		Optional<Food> maybeClosest = getClosestFood(head, foodManager) ;
		Optional<Point2D> maybeThreat = getClosestThreat(head, foodManager, snakes) ;
		if (maybeClosest.isPresent()) {
			Food closest = maybeClosest.get() ;
			double targetAngle = this.angleTo(closest.getX() + closest.getSize(), closest.getY() + closest.getSize()) ;
			targetAngle += (r.nextDouble() - 0.5) ; // ra jouter un peu de hasard pour plus de 'réalisme'
			// Utiliser l'interpolation linéaire pour lisser le mouvement
			head.setAngle(head.getAngle() * 0.9 + targetAngle * 0.1) ;
			//head.setAngle(targetAngle) ;
		}

		if (maybeThreat.isPresent()) {
			Point2D threat = maybeThreat.get() ;
			double targetAngle = this.angleTo(threat.getX(), threat.getY()) ;
			double distance = Math.sqrt(
					// On pourrait faire (segment.getX() + bodyWidth/2) - (otherSegment.getX() + bodyWidth/2)
					// pour bien faire la distance entre les centres, mais si on regarde, on peut voir que
					// les bodyWidth/2 s'annulent, donc il n'y a pas besoin de les mettre.
					Math.pow(head.getX() - threat.getX(), 2) +
							Math.pow(head.getY() - threat.getY(), 2)
			);
			//targetAngle += r.nextDouble(-0.5, 0.5); // r ajouter un peu de hasard pour plus de 'réalisme'
			targetAngle += Math.PI;
			double t = Math.min(0.2, Math.exp(-distance / 50)) ;
			// Utiliser l 'interpolation linéaire pour lisser le mouvement
			head.setAngle(head.getAngle() * (1-t) + targetAngle * t) ;
		}

		this.move(settings);
		this.eat(settings, foodManager) ;
	}

	public void setRandomSeed(long seed) {
		this.r = new Random(seed) ;
	}
}
