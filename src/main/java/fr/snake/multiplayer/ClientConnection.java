package fr.snake.multiplayer;

import fr.snake.Food;
import fr.snake.multiplayer.packet.ChangeSnakePacket;
import fr.snake.multiplayer.packet.client.JoinPacket;
import fr.snake.multiplayer.packet.server.SetFoodPacket;
import fr.snake.multiplayer.packet.server.SnakeRemovedPacket;
import fr.snake.snakes.Snake;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.server.ServerSettingsPacket;
import fr.snake.multiplayer.packet.server.SnakeSpawnedPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

/**
 * Connexion d'un serveur vers le client
 */
public class ClientConnection implements Runnable {

	private Server server ;
	private Socket socket ;
	private DataInputStream dis;
	private DataOutputStream dos;
	/**
	 * Le serpent associé à ce client.
	 */
	private Snake associatedSnake;

	public ClientConnection(Server server, Socket socket) throws IOException {
		this.server = server ;
		this.socket = socket ;
		this.dis = new DataInputStream(socket.getInputStream()) ;
		this.dos = new DataOutputStream(socket.getOutputStream()) ;
	}

	/**
	 * Cette méth ode envoie un paquet.
	 * Son envoi est garanti comme étant dans l'ordre
	 * @param packet le paquet à e nvoyer
	 * @throws IOException
	 */
	public void sendPacket(Packet packet) throws IOException {
		// L 'utilisation de synchronized (this) permet de bloquer l'objet jusqu'à ce que le paquet soit
		// en tièrement envoyé. Cela évite que deux paquets soient envoyés en meme temps et se mélangent
		// et d'autres problèmes de co ncurrence.
		synchronized (this) {
			Packet.write(dos, packet) ;
		}
	}

	/**
	 * Informe le client du changement d'un serpent
	 * @param snake le serpent changé
	 * @throws IOException
	 */
	public void changeSnake(Snake snake) throws IOException {
		if (snake == associatedSnake) return; // le client ne reçoit pas de p aquet sur son propre serpent
		ChangeSnakePacket packet = new ChangeSnakePacket() ;
		packet.setSnake(snake) ;
		sendPacket(packet) ;
	}

	/**
	 * Informe le client de l' ajout d'un se rpent
	 * @param snake le serpent ajouté
	 * @throws IOException
	 */
	public void spawnSnake(Snake snake) throws IOException {
		if (snake == associatedSnake) return; // le client ne reçoit pas de paquet sur son propre serpent
		SnakeSpawnedPacket packet = new SnakeSpawnedPacket() ;
		// Le seul paquet avec isPlayer = true est envoyé dans ServerConnection.run()
		packet.setIsPlayer(false) ;
		packet.setSnake(snake) ;
		sendPacket(packet) ;
	}

	/**
	 * Informe le client du retrait d'un serpent
	 * @param uuid l'UUID du se rpent retiré
	 * @throws IOException
	 */
	public void removeSnake(UUID uuid) throws IOException {
		SnakeRemovedPacket packet = new SnakeRemovedPacket() ;
		packet.setUuid(uuid) ;
		sendPacket(packet) ;
	}

	/**
	 * Informe le client de la nourriture sur la carte
	 * @param foods n ourriture sur la carte
	 * @throws IOException
	 */
	public void setFood(Food[] foods) throws IOException {
		SetFoodPacket packet = new SetFoodPacket() ;
		packet.setFoods(foods) ;
		sendPacket(packet) ;
	}

	/**
	 * Déconnecte le client
	 */
	public void disconnect() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Impossible de fermer le socket.") ;
			}
		}
		server.removeClient(this) ;
	}

	public void run() {
		// Tenter d'activer TCP_NODELAY
		// C'est une chose impo rtante à faire pour les connexions TCP pour les jeux vidéos
		// car, cela permet de désactiver l'algorithme de Nagle, qui est un algorithme qui rajoute
		// intentio nnellement de la latence, ce qui est bon pour certains p rotocoles (FTP, HTTP...)
		// mais qui n'est pas so uhaitable pour un jeu en temps réel.
		try {
			socket.setTcpNoDelay(true) ;
		} catch (SocketException e) {
			System.err.println("Impossible d'activer TCP _NODELAY.") ;
			e.printStackTrace() ;
		}

		try {
			// Dès la connexion, on envoie un packet ServerSettingsPacket pour que
			// le client puisse charger les bons paramètres.
			ServerSettingsPacket ss = new ServerSettingsPacket() ;
			ss.setSettings(server.getSettings()) ;
			sendPacket(ss) ;

			// Faire spawn tous les se rpents déjà présents
			for (Snake snake : server.getState().getSnakes()) {
				SnakeSpawnedPacket spawn = new SnakeSpawnedPacket() ;
				spawn.setIsPlayer(false) ;
				spawn.setSnake(snake) ;
				sendPacket(spawn) ;
			}
			server.addClient(this) ;

			// Envoie la nourriture présente sur la carte
			this.setFood(server.getState().getFoods()) ;

			while (true) {
				Packet packet = Packet.read(dis) ;
				switch (packet.getType()) {
					case JOIN:
						associatedSnake = Snake.createPlayerSnake(server.getSettings(), ((JoinPacket) packet).getUsername()) ;

						// On envoie man uellement le paquet SnakeSpawnedPacket au joueur spécifi quement
						// pour qu'il prenne connaissance de son serpent.
						SnakeSpawnedPacket spawn = new SnakeSpawnedPacket() ;
						spawn.setIsPlayer(true) ;
						spawn.setSnake(associatedSnake) ;
						Packet.write(dos, spawn) ;

						server.getState().addSnake(associatedSnake) ;
						break;
					case CHANGE_SNAKE:
						try {
							server.getState().changeSnake(((ChangeSnakePacket) packet).getSnake()) ;
						} catch (IllegalStateException e) {
							// Le client à es sayer de changer un serpent qui n'existe plus, ce qui peut arriver
							// lorsqu 'un paquet est envoyé lorsque le se rpent est t oujours vivant, mais qu'entre le
							// moment où il est envoyé et le moment où il est reçu, le se rpent meurt.
							// Dans ce cas-là, on ne fait rien.
						}
						break;
					default:
						throw new IllegalStateException() ;
				}
				System.out.println(packet) ;
			}
		} catch (EOFException e) {
			System.out.println("Connexion avec le client subitement fermée.") ;
		} catch (IOException e) {
			System.err.println("Erreur d'E/S avec le socket. Fermeture de la connexion avec le client.") ;
			e.printStackTrace() ;
		} catch (IllegalStateException e) {
			System.err.println("Le client a tenté une opération invalide. Fermeture de la connexion.") ;
			e.printStackTrace() ;
		}

		disconnect() ;
	}

	public Snake getAssociatedSnake() {
		return associatedSnake;
	}

}
