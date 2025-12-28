package fr.snake;

import fr.snake.states.*;

import java.awt.Graphics;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.*;

public class App extends JPanel implements MouseMotionListener, MouseListener, ComponentListener, KeyListener, Runnable, StateChanger {
	private static final long serialVersionUID = 1L ;
	private final long fps ;
	private State state ;
	private Settings settings ;

	public App() {
		fps = 30 ;

		File file = new File("settings.json") ;
		if (file.exists()) {
			try {
				settings = Settings.fromFile(file) ;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erreur a la lecture des paramètres. Réinitialisation des paramètres.") ;
				settings = new Settings() ;
			}
		} else {
			settings = new Settings() ;
		}
		state = new MainMenuState(settings, this) ;
		// Lancer l 'explication du jeu, si le jeu n'a pas encore été expliqué
		if (!settings.isExplained()) {
			state = new ExplanationState(settings, this) ;
		}
		Music.play(Settings.BGM_URL, true) ;
	}

	public static void main(String[] args) {
		App app = new App() ;
		JFrame frame = new JFrame() ;
		frame.add(app) ;

		frame.setSize(app.settings.getWindowWidth(), app.settings.getWindowHeight()) ;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
		frame.setLocationRelativeTo(null) ;
		frame.setVisible(true) ;
		app.addMouseMotionListener(app) ;
		app.addMouseListener(app) ;
		app.addComponentListener(app) ;
		frame.addKeyListener(app) ;
		app.run() ;
	}

	/**
	 * Lance la boucle pr incipale du jeu.
	 */
	@Override
	public void run() {
		while (true) {
			repaint() ;
			try {
				Thread.sleep(1000 / fps) ;
			}
			catch (InterruptedException e) {
				// Le thread a pplicatif n'est interrompu par aucun thread, c'est exception n'est donc en circonstance
				// normales jamais atteinte.
				e.printStackTrace() ;
			}
		}
	}

	/**
	 * Cette fonction dessine l 'interface du jeu et le jeu en lui-meme.
	 * @param g L'objet <code> Graphics</code> à utiliser pour peindre le contenu.
	 */
	@Override
	public void paint(Graphics g) {
		state.update() ;
		state.paint(g) ;
	}

	/**
	 * Change l'état actuel du jeu
	 * @param newState nouvel état
	 */
	@Override
	public void setState(State newState) {
		this.state = newState ;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		state.onMouseMove(e.getX(), e.getY()) ;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		state.onMouseMove(e.getX(), e.getY()) ;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		state.onMouseClick(e.getX(), e.getY()) ;
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {
		this.state.onWindowResize(getWidth(), getHeight()) ;
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		this.state.onKeyPressed(e.getKeyCode()) ;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.state.onKeyReleased(e.getKeyCode()) ;
	}
}
