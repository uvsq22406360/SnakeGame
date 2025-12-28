package fr.snake.states;

import fr.snake.DeathScreen;
import fr.snake.Settings;
import fr.snake.snakes.Snake;
import fr.snake.segments.Segment;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SoloGameState extends PlayerGameState {
	/**
	 * Est -ce que le jeu est en pause
	 */
	private boolean paused = false;

	public SoloGameState(Settings settings, StateChanger stateChanger, String username) {
		super(settings, stateChanger, username) ;

		player = Snake.createPlayerSnake(settings, username) ;
		addSnake(player) ;
	}

	@Override
	public void replay() {
		player = Snake.createPlayerSnake(settings, username) ;
		addSnake(player) ;
	}

	@Override
	public void update() {
		if (paused) return;
		super.update() ;
		if (player != null && player.isAlive()) {
			deathScreen = null;
			player.moveAndCheckCollisions(settings, snakes, foodManager) ;
			centerCameraOn(player) ;
		}
	}

	@Override
	public void onKeyPressed(int code) {
		super.onKeyPressed(code) ;
		if (code == KeyEvent.VK_SPACE) {
			// On ne peut pas mettre en pause si on est mort
			if (deathScreen == null)
				paused = !paused ;
		}
	}

	@Override
	public void onMouseMove(int mouseX, int mouseY) {
		if (!paused) super.onMouseMove(mouseX, mouseY) ;
	}

	@Override
	public void onMouseClick(int mouseX, int mouseY) {
		if (!paused) super.onMouseClick(mouseX, mouseY) ;
	}
}
