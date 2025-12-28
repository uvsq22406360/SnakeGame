package fr.snake.states;

import fr.snake.Background;
import fr.snake.Settings;
import fr.snake.Skin;
import fr.snake.segments.Segment;
import fr.snake.snakes.Snake;
import fr.snake.ui.Button;
import fr.snake.ui.SettingsDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class MainMenuState extends State {
	private Button soloButton ;
	private Button multiButton ;
	private Button settingsButton ;
	private Button changeUsernameButton ;
	private Button previousSkinButton ;
	private Button nextSkinButton ;
	private Button map1Button ;
	private Button map2Button ;
	private Settings settings ;
	private StateChanger stateChanger ;
	private Snake demoSnake ;
	private Background background ;
	private double dx, dy ;
	private String username = "" ;

	public MainMenuState(Settings settings, StateChanger stateChanger) {
		this.settings = settings ;
		this.stateChanger = stateChanger ;
		soloButton = new Button(0, 0, 200, 50, "Solo") ;
		soloButton.setOnClick(() -> {
			this.stateChanger.setState(new SoloGameState(this.settings, this.stateChanger, username)) ;
		});

		multiButton = new Button(0, 100, 200, 50, "Multijoueur") ;
		multiButton.setOnClick(() -> {
			try {
				String address = JOptionPane.showInputDialog(null, "Adresse du serveur", "localhost") ;
				MultiplayerGameState newState = MultiplayerGameState.connect(this.stateChanger, username, InetAddress.getByName(address)) ;
				newState.setMemento(saveToMemento()) ;
				newState.settings.setWindowWidth(this.settings.getWindowWidth()) ;
				newState.settings.setWindowHeight(this.settings.getWindowHeight()) ;
				this.stateChanger.setState(newState) ;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Impossible de se connecter au serveur: " + e.getLocalizedMessage(), "Erreur", JOptionPane.ERROR_MESSAGE) ;
			}
		}) ;

		changeUsernameButton = new Button(0, 200, 100, 30, "Changer") ;
		changeUsernameButton.setOnClick(() -> {
			this.setUsername(JOptionPane.showInputDialog("Nom d'utilisateur")) ;
		});

		settingsButton = new Button(0, 300, 100, 30, "Paramètres") ;
		settingsButton.setOnClick(() -> {
			SettingsDialog.launch(this.settings) ;
		});

		nextSkinButton = new Button(0, 0, 50, 30, ">") ;
		nextSkinButton.setOnClick(() -> {
			Skin newSkin = demoSnake.getSkin().nextSkin() ;
			settings.setDefaultSkin(newSkin) ;
			demoSnake.setSkin(newSkin) ;
			try {
				settings.save(new File("settings.json")) ;
			} catch (IOException e) {
				// Impossible de sa uvegarder, ce qui n'est pas grave
				System.err.println("Impossible de sa uvegarder le skin choisi") ;
			}
		});
		previousSkinButton = new Button(0, 0, 50, 30, "<") ;
		previousSkinButton.setOnClick(() -> {
			Skin newSkin = demoSnake.getSkin().previousSkin() ;
			settings.setDefaultSkin(newSkin) ;
			demoSnake.setSkin(newSkin) ;
			try {
				settings.save(new File("settings.json")) ;
			} catch (IOException e) {
				// Impossible de sauvegarder, ce qui n'est pas grave
				System.err.println("Impossible de sa uvegarder le skin choisi") ;
			}
		});
		
		map1Button = new Button(0, 0, 60, 30, "Map 1") ;
		map1Button.setOnClick(() -> {
			settings.setDefaultMap(Background.Map.MAP_1) ;
			background = new Background(settings.getDefaultMap()) ;
			try {
				settings.save(new File("settings.json")) ;
			} catch (IOException e) {
				// Impossible de sauvegarder, ce qui n'est pas grave
				System.err.println("Impossible de sauvegarder la map choisie") ;
			}
		});
		map2Button = new Button(0, 0, 60, 30, "Map 2") ;
		map2Button.setOnClick(() -> {
			settings.setDefaultMap(Background.Map.MAP_2) ;
			background = new Background(settings.getDefaultMap()) ;
			try {
				settings.save(new File("settings.json")) ;
			} catch (IOException e) {
				// Impossible de sauvegarder, ce qui n'est pas grave
				System.err.println("Impossible de sauvegarder la map choisi e") ;
			}
		});

		demoSnake = Snake.createPlayerSnake(settings, username) ;
		background = new Background(settings.getDefaultMap()) ;
	}

	public SettingsMemento saveToMemento() {
		return new SettingsMemento(settings) ;
	}

	public void restoreFromMemento(SettingsMemento memento) {
		this.settings = memento.getSettings() ;
	}

	@Override
	public void update() {
		// Faire l 'agencement des b outons
		int width = settings.getWindowWidth(), height = settings.getWindowHeight() ;
		soloButton.setX(width / 2 - soloButton.getWidth() / 2) ;
		multiButton.setX(width / 2 - multiButton.getWidth() / 2) ;
		settingsButton.setX(width / 2 - settingsButton.getWidth() / 2) ;
		map1Button.setX(width / 2 - (map1Button.getWidth() + map2Button.getWidth() + 40) / 2)  ;
		map2Button.setX(map1Button.getX() + map1Button.getWidth() + 40) ;

		soloButton.setY(height - 400) ;
		multiButton.setY(height - 300) ;
		settingsButton.setY(height - 200) ;
		map1Button.setY(height - 100) ;
		map2Button.setY(height - 100) ;
	}

	@Override
	public void paint(Graphics g) {
		int width = settings.getWindowWidth(), height = settings.getWindowHeight() ;

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK) ;
		g2d.fillRect(0, 0, width, height) ;
		background.paint(g2d, settings, (int) dx, (int) dy) ;
		// Petit m ouvement de l 'arrière-plan pour fair e beau
		dx += 1.5;
		dy += 1;
		if (dx > settings.getWidth()) dx = 0;
		if (dy > settings.getHeight()) dy = 0;

		soloButton.paint(g2d, settings, 0, 0) ;
		multiButton.paint(g2d, settings, 0, 0) ;
		settingsButton.paint(g2d, settings, 0, 0) ;
		map1Button.paint(g2d, settings, 0, 0) ;
		map2Button.paint(g2d, settings, 0, 0) ;

		String texte = "Nom d'utilisateur: " + username;
		int tx = width / 2 - g2d.getFontMetrics().stringWidth(texte) / 2 ;
		g2d.setColor(Color.WHITE) ;
		g2d.drawString(texte, tx, soloButton.getY() - 30) ;
		changeUsernameButton.setX(tx + g2d.getFontMetrics().stringWidth(texte) + 20) ;
		changeUsernameButton.setY(soloButton.getY() - 50) ;
		changeUsernameButton.paint(g2d, settings, 0, 0) ;

		Segment head = demoSnake.getBody().get(0) ;
		int serpentX = width / 2 - (int) demoSnake.getBodyWidth() / 2;
		int serpentY = Math.min(height - 500, 50) ;
		demoSnake.paint(g2d, settings, (int) head.getX() - serpentX, (int) head.getY() - serpentY) ;
		previousSkinButton.setX(serpentX - 100) ;
		previousSkinButton.setY(serpentY + 30) ;
		nextSkinButton.setX(serpentX + 100) ;
		nextSkinButton.setY(serpentY + 30) ;
		previousSkinButton.paint(g2d, settings, 0, 0) ;
		nextSkinButton.paint(g2d, settings, 0, 0) ;
	}

	@Override
	public void onMouseMove(int mouseX, int mouseY) {

	}

	@Override
	public void onMouseClick(int mouseX, int mouseY) {
		soloButton.onMouseClick(mouseX, mouseY) ;
		multiButton.onMouseClick(mouseX, mouseY) ;
		settingsButton.onMouseClick(mouseX, mouseY) ;
		changeUsernameButton.onMouseClick(mouseX, mouseY) ;
		previousSkinButton.onMouseClick(mouseX, mouseY) ;
		nextSkinButton.onMouseClick(mouseX, mouseY) ;
		map1Button.onMouseClick(mouseX, mouseY) ;
		map2Button.onMouseClick(mouseX, mouseY) ;
	}

	@Override
	public void onWindowResize(int width, int height) {
		settings.setWindowWidth(width) ;
		settings.setWindowHeight(height) ;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username ;
		demoSnake.setUsername(username) ;
	}
}
