package fr.snake.multiplayer;

import fr.snake.Food;
import fr.snake.Settings;
import fr.snake.snakes.Snake;
import fr.snake.states.GameState;
import fr.snake.states.ServerGameState;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Server {

	/**
	 * Le port sur lequel le serveur va é couter.
	 */
	public static int SERVER_PORT = 23130;

	/**
	 * B ooléan qui indique si le serveur est a ctuellement en train de tourner.
	 */
	private volatile boolean running;
	/**
	 * Les paramètres qu'utilise le serveur.
	 */
	private Settings settings;
	/**
	 * L'état du jeu représenté par le serveur.
	 */
	private GameState state;
	/**
	 * La liste des clients c onnectés. Cette liste est implémentée par une CopyOnWriteArrayList car étant donné qu'il
	 * y a peu de clients, la copie a peu de charge en resso urces et permet donc de fa cilement utiliser cette variable
	 * sur plusieurs threads sans s ynchronisation.
	 */
	private List<ClientConnection> clients = new CopyOnWriteArrayList<>() ;
	/**
	 * Service d'exécuteur qui va s'occuper du multithreading.
	 */
	private ExecutorService executor;

	public Server(Settings settings) {
		running = false;
		this.settings = settings;
		state = new ServerGameState(settings, this) ;
		executor = Executors.newCachedThreadPool() ;
		state.getFoodManager().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				// La nourriture sur la carte a été changée (rajoutée ou enlevée)
				// On en informe donc les clients connectés
				sendSetFood();
			}
		});
	}

	public void start() {
		running = true;
		executor.execute(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(SERVER_PORT) ;
				serverSocket.setReuseAddress(true) ;

				while (running) {
					Socket socket = serverSocket.accept() ;
					ClientConnection client = new ClientConnection(this, socket) ;
					executor.execute(client);
				}
			} catch (IOException e) {
				System.err.println("Erreur lors de la création du serveur") ;
				e.printStackTrace() ;
			}
		});
	}

	public void stop() {
		for (ClientConnection client : clients) {
			client.disconnect() ;
		}
		running = false;
		executor.shutdown() ;
	}

	long lastPositionsSent;
	public void run(long fps) {
		while (true) {
			state.update() ;

			// Force un repositionnement de tous les serpents toutes les 100 ms (=0.1 s)
			// Cela permet d'éviter les désynchronisations
			if (System.currentTimeMillis() >= lastPositionsSent + 100) {
				lastPositionsSent = System.currentTimeMillis() ;
				for (Snake snake : state.getSnakes()) {
					sendSnakeChanged(snake) ;
				}
				// Resynchronisation de la nourriture
				this.sendSetFood();
			}
			try {
				Thread.sleep(1000 / fps) ;
			} catch (InterruptedException e) {
				throw new RuntimeException(e) ;
			}
		}
	}

	public Settings getSettings() {
		return settings;
	}

	/**
	 * Envoie à tous les clients connectés un paquet pour changer les propriétés d'un serpent.
	 * @param snake
	 */
	public void sendSnakeChanged(Snake snake) {
		for (ClientConnection client : clients) {
			try {
				client.changeSnake(snake) ;
			} catch (IOException e) {
				client.disconnect() ;
			}
		}
	}

	/**
	 * E nvoie à tous les clients connectés un paquet indiquant qu'un serpent est apparu.
	 * @param snake le serpent qui est apparu
	 */
	public void sendSnakeSpawned(Snake snake) {
		for (ClientConnection client : clients) {
			try {
				client.spawnSnake(snake) ;
			} catch (IOException e) {
				client.disconnect() ;
			}
		}
	}

	/**
	 * Envoie à tous les clients co nnectés un paquet indiquant qu'un serpent a été retiré.
	 * @param uuid l'UUID du serpent qui a été retiré
	 */
	public void sendSnakeRemoved(UUID uuid) {
		for (ClientConnection client : clients) {
			try {
				client.removeSnake(uuid) ;
			} catch (IOException e) {
				client.disconnect() ;
			}
		}
	}

	/**
	 * Envoie à tous les clients connectés un paquet mettant à jour la nourriture présente sur la carte.
	 */
	public void sendSetFood() {
		Food[] foods = state.getFoods() ;
		for (ClientConnection client : clients) {
			try {
				client.setFood(foods) ;
			} catch (IOException e) {
				client.disconnect() ;
			}
		}
	}

	public GameState getState() {
		return state;
	}
	
	public void addClient(ClientConnection client) {
		clients.add(client) ;
	}
	
	public void removeClient(ClientConnection client) {
		clients.remove(client) ;
		if (client.getAssociatedSnake() != null) {
			state.removeSnake(client.getAssociatedSnake().getUuid()) ;
		}
	}

	public static void main(String[] args) {
		Settings settings;
		File file = new File("server_settings.json") ;
		if (file.exists()) {
			try {
				settings = Settings.fromFile(file) ;
			} catch (IOException e) {
				System.err.println("Erreur lors de la lecture des paramètres,") ;
				settings = new Settings() ;
				settings.setMinimumSnakes(0) ;
			}
		} else {
			settings = new Settings() ;
			settings.setMinimumSnakes(0) ;
		}

		Server server = new Server(settings) ;
		server.start() ;
		System.out.println("Serveur démarré") ;

		server.run(30) ;
	}

}
