package fr.snake.states;

import fr.snake.Settings;
import fr.snake.snakes.Snake;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

	@Test
	public void addSnake() {
		Settings settings = new Settings();
		GameState state = new GameState(settings);
		int number = state.getSnakes().length;
		Snake newSnake = Snake.createAISnake(settings);
		state.addSnake(newSnake);
		assertEquals(number + 1, state.getSnakes().length);
	}

	@Test
	void removeSnake() {
		Settings settings = new Settings();
		settings.setMinimumSnakes(1);

		GameState state = new GameState(settings);
		state.update(); // devrait créer un serpent IA

		assertEquals(1, state.getSnakes().length);
		UUID uuid = state.getSnakes()[0].getUuid();
		state.removeSnake(uuid);
		assertEquals(0, state.getSnakes().length);
		state.removeSnake(uuid); // ne dois rien faire : pas d'erreur, rien
		assertEquals(0, state.getSnakes().length);
	}

	@Test
	void getSnakeByUuid() {
		Settings settings = new Settings();
		settings.setMinimumSnakes(1);

		GameState state = new GameState(settings);
		state.update(); // devrait créer un serpent IA

		assertEquals(1, state.getSnakes().length);
		Snake snake = state.getSnakes()[0];
		assertTrue(state.getSnakeByUuid(snake.getUuid()).isPresent());
		state.removeSnake(snake.getUuid());
		assertFalse(state.getSnakeByUuid(snake.getUuid()).isPresent());
	}

	@Test
	void changeSnake() {
		Settings settings = new Settings();
		settings.setMinimumSnakes(1);

		GameState state = new GameState(settings);
		state.update(); // devrait créer un serpent IA

		assertEquals(1, state.getSnakes().length);
		Snake snake = state.getSnakes()[0];
		Snake betterSnake = snake.clone();
		betterSnake.setBodyWidth(100);
		state.changeSnake(betterSnake);
		assertEquals(100, state.getSnakeByUuid(snake.getUuid()).get().getBodyWidth());
	}
}