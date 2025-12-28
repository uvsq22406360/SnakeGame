package fr.snake.states;

import fr.snake.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * L' état qui s'occupe d'afficher les ex plications du gameplay.
 */
public class ExplanationState extends State {
	/**
	 * Image de l'explication
	 */
	protected BufferedImage img;
	protected StateChanger stateChanger;
	protected Settings settings;

	public ExplanationState(Settings settings, StateChanger stateChanger) {
		this.settings = settings;
		this.stateChanger = stateChanger;
		try {
			this.img = ImageIO.read(this.getClass().getResourceAsStream("/Interface/explanation.png")) ;
		} catch (Exception e) {
			System.err.println("Fichier manquant: explanation.png") ;
			this.img = null; // image manquante
		}
	}

	@Override
	public void update() {
		// Si l 'image n'a pas pu charger, on passe di r ectement au menu principal
		if (img == null) {
			stateChanger.setState(new MainMenuState(settings, stateChanger)) ;
		}
	}

	@Override
	public void paint(Graphics g) {
		int width = settings.getWindowWidth(), height = settings.getWindowHeight() ;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height) ;

		if (img != null) {
			double ratio = (double)img.getWidth() / (double)img.getHeight() ;
			if (height * ratio > width) {
				int ih = (int) (width / ratio) ;
				g.drawImage(img, 0, height / 2 - ih / 2, width, ih, null) ;
			} else {
				int iw = (int) (height * ratio) ;
				g.drawImage(img, width / 2 - iw / 2, 0, iw, height, null) ;
			}
		}
	}

	public void onMouseClick(int mouseX, int mouseY) {
		next() ;
	}

	public void onWindowResize(int width, int height) {
		settings.setWindowWidth(width) ;
		settings.setWindowHeight(height) ;
	}

	public void onKeyPressed(int code) {
		if (code == KeyEvent.VK_SPACE) next() ;
	}

	public void next() {
		// settings.setExplained(true);
		stateChanger.setState(new MainMenuState(settings, stateChanger)) ;
	}
}
