package fr.snake.ui;

import fr.snake.Settings;
import fr.snake.Sprite;

import java.awt.*;

/**
 * Un bouton affiché ma nuellement par le jeu, pour que l'UI reste dans la palette g raphique du jeu.
 */
public class Button implements Sprite {
	private int x ;
	private int y ;
	private int width ;
	private int height ;
	private String text ;
	private Runnable onClick ;
	private Font font ;

	public Button(int x, int y, int width, int height, String text) {
		this.x = x ;
		this.y = y ;
		this.width = width ;
		this.height = height ;
		this.text = text ;
		this.font = new Font("Arial", Font.PLAIN, 16) ;
	}

	/**
	 * Affiche le bouton à l'écran.
	 * @param g L'objet <code>Graphics2D</code> pour dessiner le sprite
	 * @param settings Les p aramètres à prendre en compte
	 * @param dx Le défilement horizontal
	 * @param dy Le défilement vertical
	 */
	@Override
	public void paint(Graphics2D g, Settings settings, int dx, int dy) {
		g.setColor(Color.WHITE) ;
		g.fillRect(x, y, width, height) ;
		g.setColor(Color.BLACK) ;

		//g.setFont(font);
		int textWidth = g.getFontMetrics().stringWidth(text) ;
		int textHeight = g.getFontMetrics().getHeight() ;
		int tx = x + width / 2 - textWidth / 2 ;
		int ty = y + height / 2 + textHeight / 2 ;
		g.drawString(text, tx, ty) ;
	}

	public void onMouseClick(int x, int y) {
		if (x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height) {
			onClick.run() ;
		}
	}

	public int getX() {
		return x ;
	}

	public void setX(int x) {
		this.x = x ;
	}

	public int getY() {
		return y ;
	}

	public void setY(int y) {
		this.y = y ;
	}

	public int getWidth() {
		return width ;
	}

	public void setWidth(int width) {
		this.width = width ;
	}

	public int getHeight() {
		return height ;
	}

	public void setHeight(int height) {
		this.height = height ;
	}

	public String getText() {
		return text ;
	}

	public void setText(String text) {
		this.text = text ;
	}

	public Runnable getOnClick() {
		return onClick ;
	}

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick ;
	}
}
