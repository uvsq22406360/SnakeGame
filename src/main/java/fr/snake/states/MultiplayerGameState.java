package fr.snake.states;

import fr.snake.Settings;
import fr.snake.snakes.Snake;
import fr.snake.multiplayer.Server;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.server.ServerSettingsPacket;
import fr.snake.snakes.SnakeListener;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiplayerGameState extends PlayerGameState implements SnakeListener {
	/**
	 * La connexion au serveur.
	 */
	protected ServerConnection connection;
	/**
	 * La dernière fois que la position du serpent a été envoyée au serveur. Il est important
	 * de le faire périodiquement afin d'éviter toute dé synchronisation.
	 */
	private long lastPositionSend;
	/**
	 * Le memento du menu principal. Utilisé pour re staurer les paramètres du client (au lieu du serveur)
	 */
	private SettingsMemento memento;

	private MultiplayerGameState(Settings settings, StateChanger stateChanger, String username, ServerConnection connection) {
		super(settings, stateChanger, username) ;
		this.connection = connection ;
		this.snakes = new CopyOnWriteArrayList<>() ;
	}

	public static MultiplayerGameState connect(StateChanger stateChanger, String username, InetAddress address) throws IOException, IllegalStateException {
		Socket socket = new Socket(address, Server.SERVER_PORT) ;
		// Désactive l 'algorithme de Nagel. Pour voir les raisons regarder dans la classe fr.snake.multiplayer.ClientThread
		socket.setTcpNoDelay(true) ;

		Settings settings;
		// On va lire le paquet Server Settings Packet qui doit OBLIGATOIREMENT être
		// le premier paquet envoyé par le serveur
		DataInputStream dis = new DataInputStream(socket.getInputStream()) ;
		Packet packet = Packet.read(dis) ;
		if (packet instanceof ServerSettingsPacket) {
			settings = ((ServerSettingsPacket) packet).getSettings() ;
		} else {
			throw new IllegalStateException("ServerSettingsPacket doit être le premier paquet envoyé");
		}

		ServerConnection connection = new ServerConnection(socket) ;
		MultiplayerGameState state = new MultiplayerGameState(settings, stateChanger, username, connection) ;
		connection.start(state) ;
		connection.join(username) ;
		return state;
	}

	public void setMemento(SettingsMemento memento) {
		this.memento = memento;
	}

	/**
	 * Les dé placements sont in terpolés (et pour l'IA meme simulé) pour donner une imp ression de f luidité.
	 * Le serveur fait ce pendant toujours autorité !
	 */
	public void interpolate() {
		for (Snake snake : snakes) {
			if (snake.isAlive() && snake != player) {
				snake.move(settings) ;
			}
		}
	}

	@Override
	public void update() {
		interpolate() ;

		// Les mises à jour ne sont pas gérées par le client sauf pour le mouvement du joueur
		if (player != null && player.isAlive()) {
			deathScreen = null;
			player.moveAndCheckCollisions(settings, snakes, foodManager);
			// Sur le client local, le joueur mange immédiatement au lieu d'attendre le serveur
			player.eat(settings, foodManager) ;
			centerCameraOn(player) ;
		}

		// On resynchronise de force la position toutes les 0.2 s
		if (System.currentTimeMillis() >= lastPositionSend + 200 && player != null) {
			try {
				connection.changeSnake(player) ;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Il y a eu une erreur dans l 'interface réseau avec le serveur", "Erreur",
						JOptionPane.ERROR_MESSAGE) ;
				connection.disconnect() ;
			}
			lastPositionSend = System.currentTimeMillis() ;
		}
	}

	@Override
	public void replay() {
		try {
			connection.join(username) ;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				"Il y a eu une erreur dans l'interface réseau avec le serveur", "Erreur" ,
				JOptionPane.ERROR_MESSAGE) ;
			connection.disconnect() ;
		}
	}

	@Override
	public void quit() {
		connection.disconnect() ;
		MainMenuState newState = new MainMenuState(settings, stateChanger) ;
		newState.setUsername(username) ;
		newState.restoreFromMemento(memento) ;
		stateChanger.setState(newState) ;
	}

	@Override
	public void setPlayer(Snake player) {
		super.setPlayer(player);
		player.addSnakeListener(this) ;
	}

	@Override
	public void snakeHeadMoved(Snake snake, double newX, double newY) {}

	@Override
	public void snakeSetDirection(Snake snake, double angle) {
		try {
			connection.changeSnake(snake) ;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				"Il y a eu une erreur dans l'interface réseau avec le serveur", "Erreur",
				JOptionPane.ERROR_MESSAGE) ;
			connection.disconnect() ;
		}
	}
}
