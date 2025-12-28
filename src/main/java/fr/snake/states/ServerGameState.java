package fr.snake.states;

import fr.snake.Settings;
import fr.snake.multiplayer.Server;
import fr.snake.snakes.Snake;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * L' état du jeu tel que re présenté sur le serveur.
 * Cet état n'est utilisé que sur le serveur.
 */
public class ServerGameState extends GameState {
	private Server server ;

	public ServerGameState(Settings settings, Server server) {
		super(settings) ;
		this.snakes = new CopyOnWriteArrayList<>() ;
		this.server = server ;
	}

	public void addSnake(Snake snake) {
		super.addSnake(snake) ;
		server.sendSnakeSpawned(snake) ;
	}

	public void removeSnake(UUID uuid) {
		super.removeSnake(uuid) ;
		server.sendSnakeRemoved(uuid) ;
	}

	public Server getServer() {
		return server;
	}

}
