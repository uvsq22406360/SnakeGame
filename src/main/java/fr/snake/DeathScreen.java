package fr.snake;

import fr.snake.states.PlayerGameState;
import fr.snake.ui.Button;

import java.awt.*;

/**
 * Écran affiché lorsque le serpent du joueur meurt. Il permet soit de rejouer, soit de quitter la p artie.
 */
public class DeathScreen implements Sprite {
	private Button replayButton ;
	private Button quitButton ;
	private PlayerGameState state ;
	private int score;

	public DeathScreen(PlayerGameState state, int score) {
		this.state = state ;
		this.score = score ;
		replayButton = new Button(0, 0, 200, 50, "Rejouer") ;
		replayButton.setOnClick(() -> {
			state.replay();
		});
		quitButton = new Button(0, 100, 200, 50, "Quitter") ;
		quitButton.setOnClick(() -> {
			state.quit() ;
		});
	}

	/**
	 * Affiche l'écran de mort
	 * @param g L'objet <code> Graphics2D </code> pour dessiner le sprite
	 * @param settings Les paramètres à prendre en compte
	 * @param dx Le dé filement horizontal
	 * @param dy Le dé filement vertical
	 */
	@Override
	public void paint(Graphics2D g, Settings settings, int dx, int dy) {
		int width = settings.getWindowWidth(), height = settings.getWindowHeight();
		g.setColor(new Color(0, 0, 0, 128)) ;
		g.fillRect(0, 0, width, height) ;

		String texte = "Vous êtes mort. Score: " + score ;
		g.setColor(Color.WHITE);
		g.drawString(texte, width / 2 - g.getFontMetrics().stringWidth(texte) / 2, height / 2) ;

		replayButton.setX(width / 2 - replayButton.getWidth() / 2) ;
		replayButton.setY(height / 2 + 50) ;
		quitButton.setX(width / 2 - quitButton.getWidth() / 2) ; 
		quitButton.setY(height / 2 + 150) ;
		replayButton.paint(g, settings, dx, dy) ;
		quitButton.paint(g, settings, dx, dy) ;
	}

	public void onMouseClick(int x, int y) {
		replayButton.onMouseClick(x, y) ;
		quitButton.onMouseClick(x, y) ;
	}
}
