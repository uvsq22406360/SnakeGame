package fr.snake.segments;

import fr.snake.FoodManager;
import fr.snake.Settings;
import fr.snake.snakes.Snake;

import java.io.Serializable;
import java.util.Objects;

/**
 * La classe <code>Segment</code> re présente un segment constituant un serpent.
 */
public abstract class Segment implements Serializable {
	private static final long serialVersionUID = -186790183120066697L;
	private double x ;
	private double y ;
	private double angle ;

	public Segment(double x, double y, double angle) {
		this.x = x ;
		this.y = y ;
		this.angle = angle*Math.PI/180; // convertir les angles en radians
	}

	public double getX() {
		return x ;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y ;
	}

	public void setY(double y) {
		this.y = y ;
	}

	public double getAngle() {
		return angle ;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Re nvoie vrai si ce type de segment entraine une collision (et donc la mort du serpent s'il y a le type de se gment par
	 * défaut) et faux sinon.
	 * @return
	 */
	public boolean causesCollision() {
		return true;
	}

	/**
	 * Ce qu'il faut faire en cas de collision.
	 * Cette mé thode n'est appellée que si le segment est en tete.
	 * @param thisSnake Le serpent qui entre en c ollision avec un autre serpent
	 * @param collidedSegment Le se gment de l'autre serpent avec lequel on est rentré en collision
	 * @param settings
	 * @param foodManager
	 */
	public abstract void onCollision(Snake thisSnake, Segment collidedSegment, Settings settings, FoodManager foodManager) ;

	/**
	 * Méthode appelée lorsqu'un autre serpent se heurte à notre segment.
	 * @param snake Le serpent qui est entré en co llision avec nous
	 * @param collidedSegment
	 * @param settings
	 * @param foodManager
	 */
	public void onCollided(Snake snake, Segment collidedSegment, Settings settings, FoodManager foodManager) {
		// Par défaut, ne rien faire.
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Segment segment = (Segment) o;
		return Double.compare(x, segment.x) == 0 && Double.compare(y, segment.y) == 0 && Double.compare(angle, segment.angle) == 0 ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, angle) ;
	}
}
