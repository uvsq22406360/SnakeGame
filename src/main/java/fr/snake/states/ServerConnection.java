package fr.snake.states;

import fr.snake.multiplayer.packet.ChangeSnakePacket;
import fr.snake.multiplayer.packet.server.SetFoodPacket;
import fr.snake.multiplayer.packet.server.SnakeRemovedPacket;
import fr.snake.snakes.Snake;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.client.JoinPacket;
import fr.snake.multiplayer.packet.server.SnakeSpawnedPacket;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Une connexion du client à un serveur distant.
 */
public class ServerConnection implements Runnable {
	private MultiplayerGameState state ;
	private Socket socket ;
	private Thread thread ;
	private DataInputStream dis ;
	private DataOutputStream dos;

	public ServerConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.thread = new Thread(this) ;
		this.thread.setName("Client-Thread") ;
		this.dis = new DataInputStream(socket.getInputStream()) ;
		this.dos = new DataOutputStream(socket.getOutputStream()) ;
	}

	public void start(MultiplayerGameState state) {
		this.state = state ;
		thread.start() ;
	}

	/**
	 * C ette méthode envoie un paquet.
	 * Son envoi est ga ranti comme étant dans l'ordre
	 * @ param packet le paquet à envoyer
	 * @throws IOException
	 */
	public void sendPacket(Packet packet) throws IOException {
		// L 'utilisation de synchronized (this) permet de bloquer l'objet jusqu'à ce que le paquet soit
		// ent ièrement envoyé. Cela évite que deux paquets soient envoyés en meme temps et se mé  langent
		// et d'autres problèmes de co ncurrence.
		synchronized (this) {
			Packet.write(dos, packet) ;
		}
	}

	public void join(String username) throws IOException {
		JoinPacket packet = new JoinPacket() ;
		packet.setUsername(username) ;
		sendPacket(packet) ;
	}

	public void changeSnake(Snake snake) throws IOException {
		ChangeSnakePacket packet = new ChangeSnakePacket() ;
		packet.setSnake(snake) ;
		sendPacket(packet) ;
	}

	/**
	 * Se déconnecte du serveur
	 */
	public void disconnect() {
		if (!socket.isClosed()) {
			try {
				socket.close() ;
			} catch (IOException e) {
				System.err.println("Impossible de fermer le socket.") ;
			}
		}
	}

	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream()) ;
			while (true) {
				Packet packet = Packet.read(dis) ;
				switch (packet.getType()) {
					case SNAKE_SPAWNED:
						SnakeSpawnedPacket spawned = (SnakeSpawnedPacket) packet ;
						state.addSnake(spawned.getSnake()) ;
						if (spawned.isPlayer()) {
							state.setPlayer(spawned.getSnake()) ;
						}
						break;
					case SNAKE_REMOVED:
						SnakeRemovedPacket removed = (SnakeRemovedPacket) packet ;
						state.removeSnake(removed.getUuid()) ;
						break;
					case CHANGE_SNAKE:
						state.changeSnake(((ChangeSnakePacket) packet).getSnake()) ;
						break;
					case SET_FOOD:
						state.getFoodManager().setFoods(((SetFoodPacket) packet).getFoods()) ;
						break;
					default:
						throw new IOException("Paquet inattendu: " + packet.getType()) ;
				}
			}
		} catch (IOException e) {
			if (!socket.isClosed()) {
				e.printStackTrace() ;
				JOptionPane.showMessageDialog(null,
						"Il y a eu une erreur dans l'interface réseau avec le serveur.", "Erreur",
						JOptionPane.ERROR_MESSAGE) ;
			}
		}

		disconnect() ;
		state.quit() ;
	}
}
