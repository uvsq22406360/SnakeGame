package fr.snake;

import fr.snake.segments.Segment;
import fr.snake.snakes.Snake;
import org.junit.Test;
import junit.framework.TestCase;

public class SnakeTest extends TestCase {
	/**
	 * Test de la méthode Snake.angleTo(x, y)
	 */
	@Test
	public void testAngleTo() {
		Settings settings = new Settings() ;

		Snake snake = Snake.createPlayerSnake(settings, "Joueur") ;
		Segment head = snake.getBody().get(0) ;

		assertEquals(0.0, snake.angleTo(head.getX() + snake.getBodyWidth() / 2 + 100, head.getY() + snake.getBodyWidth() / 2)) ;
		assertEquals(Math.PI / 2, snake.angleTo(head.getX() + snake.getBodyWidth() / 2, head.getY() + snake.getBodyWidth() / 2 + 100)) ;
		assertEquals(Math.PI, snake.angleTo(head.getX() + snake.getBodyWidth() / 2 - 100, head.getY() + snake.getBodyWidth() / 2)) ;
		assertEquals(-Math.PI / 2, snake.angleTo(head.getX() + snake.getBodyWidth() / 2, head.getY() + snake.getBodyWidth() / 2 - 100)) ;
	}

	/**
	 * Test de la méthode Snake.move(settings)
	 */
	@Test
	public void testMove() {
		Settings settings = new Settings() ;

		Snake snake = Snake.createPlayerSnake(settings, "Joueur") ;
		Segment head = snake.getBody().get(0) ;
		double oldX = head.getX(), oldY = head.getY() ;

		head.setAngle(0) ;
		snake.move(settings) ;
		assertEquals(oldX + Math.round(snake.getSpeed()), head.getX()) ;
		assertEquals(oldY + 0, head.getY()) ;

		head.setAngle(Math.PI / 2) ;
		snake.move(settings) ;
		assertEquals(oldX + Math.round(snake.getSpeed()) + 0, head.getX()) ;
		assertEquals(oldY + 0 + Math.round(snake.getSpeed()), head.getY()) ;
	}

}
