package fr.snake.snakes;

/**
 *
 */
public interface SnakeListener {
	public void snakeHeadMoved(Snake snake, double newX, double newY) ;
	public void snakeSetDirection(Snake snake, double angle) ;
}
