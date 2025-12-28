package fr.snake.states;

import fr.snake.DeathScreen;
import fr.snake.Settings;
import fr.snake.snakes.Snake;
import fr.snake.segments.Segment;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.UUID;

/**
 * État de partie de jeu en cours où on c ontrôle un joueur.
 * Cet état et ses sous-types ne sont u tilisés que sur le cl ient.
 */
public abstract class PlayerGameState extends GameState {
	/**
	 * Le serpent du joueur
	 */
	protected Snake player = null ;
	protected DeathScreen deathScreen = null ;
	protected StateChanger stateChanger ;
	protected String username ;
	/**
	 * B ooléan indiquant si la flèche gauche est appuyée
	 */
	protected boolean leftArrowPressed;
	/**
	 * B ooléan indiquant si la flèche droite est appuyée
	 */
	protected boolean rightArrowPressed ;

	public PlayerGameState(Settings settings, StateChanger stateChanger, String username) {
		super(settings) ;
		this.stateChanger = stateChanger ;
		this.username = username ;
	}

	@Override
	public void onMouseMove(int mouseX, int mouseY) {
		if (player != null)
			player.setDirection(mouseX + cameraX, mouseY + cameraY) ;
	}

	@Override
	public void onMouseClick(int mouseX, int mouseY) {
		if (deathScreen != null)
			deathScreen.onMouseClick(mouseX, mouseY) ;
		else if (player != null && player.isAlive())
			player.burnSegment(settings) ;
	}

	@Override
	public void onWindowResize(int width, int height) {
		settings.setWindowWidth(width) ;
		settings.setWindowHeight(height) ;
	}

	@Override
	public void onKeyPressed(int code) {
		if (code == KeyEvent.VK_LEFT) {
			leftArrowPressed = true ;
		}
		if (code == KeyEvent.VK_RIGHT) {
			rightArrowPressed = true ;
		}
		if (code == KeyEvent.VK_ESCAPE) {
			this.quit() ;
		}
	}

	@Override
	public void onKeyReleased(int code) {
		if (code == KeyEvent.VK_LEFT) {
			leftArrowPressed = false ;
		}
		if (code == KeyEvent.VK_RIGHT) {
			rightArrowPressed = false ;
		}
	}

	public abstract void replay() ;

	public void quit() {
		MainMenuState newState = new MainMenuState(settings, stateChanger) ;
		newState.setUsername(username) ;
		stateChanger.setState(newState) ;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g) ;
		Graphics2D g2d = (Graphics2D) g;
		if (player != null) {
			g2d.setColor(Color.WHITE) ;
			g2d.drawString("Votre longueur: " + player.getBody().size(), 0, settings.getWindowHeight()) ;
			double speedBoost = player.getSpeedBoost() ;
			if (speedBoost > 0.1) {
				g2d.setColor(new Color(255, 255, 255, (int) Math.min(255, speedBoost * 255))) ;
				g2d.drawString("Boost de vitesse: " + speedBoost, 0, settings.getWindowHeight() - 20) ;
			}
		}
		if (deathScreen != null) {
			deathScreen.paint(g2d, settings, cameraX, cameraY) ;
		}
	}

	@Override
	public void update() {
		super.update() ;
		if (player != null && player.isAlive()) {
			Segment head = player.getBody().get(0) ;
			if (leftArrowPressed) head.setAngle(head.getAngle() - 0.1) ;
			if (rightArrowPressed) head.setAngle(head.getAngle() + 0.1) ;
		}
	}

	public Snake getPlayer() {
		return player ;
	}

	public void setPlayer(Snake player) {
		this.player = player ;
	}

	@Override
	public void removeSnake(UUID uuid) {
		super.removeSnake(uuid) ;
		// Si le serpent e nlevé est celui du joueur, alors il faut mettre à jour la v ariable 'player'
		if (player != null && player.getUuid().equals(uuid)) {
			deathScreen = new DeathScreen(this, player.getBody().size() * 10) ;
			player = null ;
		}
	}
}
