package fr.snake;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Un objet affichable à l'écran.
 */
public interface Sprite {
	/**
	 * Cette méthode affiche et dessine un sprite à l'écran en utilisant les paramètres donnés.
	 * @param g L'objet <code>Graphics2D</code> pour dessiner le sprite
	 * @param settings Les paramètres à prendre en compte
	 * @param dx Le défilement horizontal
	 * @param dy Le défilement vertical
	 */
	public void paint(Graphics2D g, Settings settings, int dx, int dy) ;
}
