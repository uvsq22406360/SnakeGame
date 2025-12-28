package fr.snake;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * Back ground est la classe qui s'occupe de l'affichage de l'arrière-plan (background en anglais)
 */
public class Background implements Sprite  {

	/**
	 * Image pré -générée de l'arrière-plan
	 */
	BufferedImage img ;

	public Background(Map map) {
		try {
			BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream(map.getFileName())) ;
			BufferedImage temp = new BufferedImage(img.getWidth() * 4, img.getHeight() * 4, BufferedImage.TYPE_INT_RGB) ;

			// TODO: faire un algorithme en bonne et due forme pour faire ça peut importe la t aille du jeu
			Graphics2D g = temp.createGraphics() ;
			g.drawImage(img, 0, 0, null) ;
			g.copyArea(0, 0, img.getWidth(), img.getHeight(), img.getWidth(), 0) ;
			g.copyArea(0, 0, img.getWidth()*2, img.getHeight(), img.getWidth()*2, 0) ;
			g.copyArea(0, 0, img.getWidth()*4, img.getHeight(), 0, img.getHeight()) ;
			g.copyArea(0, 0, img.getWidth()*4, img.getHeight()*2, 0, img.getHeight()*2) ;
			g.dispose() ;
			this.img = temp ;
		}
		catch (IOException e) {
			e.printStackTrace() ;
		}
	}

	/**
	 * Affiche à l'écran l' arrière -plan (background)
	 * @param g L'objet <code> Graphics2D </code> pour dessiner le sprite
	 * @param settings Les paramètres à prendre en compte
	 * @param dx Le dé filement horizontal
	 * @param dy Le dé filement vertical
	 */
	@Override
	public void paint(Graphics2D g, Settings settings, int dx, int dy) {
		for (int x = -this.img.getWidth(); x <= this.img.getWidth(); x += this.img.getWidth()) {
			for (int y = -this.img.getHeight(); y <= this.img.getHeight(); y += this.img.getHeight()) {
				g.drawImage(this.img, -dx + x, -dy + y, null) ;
			}
		}
	}

	public enum Map {
		MAP_1("/Interface/background.png") ,
		MAP_2("/Interface/skinbg.jpg") ,
		;

		private String fileName;

		Map(String fileName) {
			this.fileName = fileName ;
		}

		public String getFileName() {
			return fileName ;
		}
	}
	
}
