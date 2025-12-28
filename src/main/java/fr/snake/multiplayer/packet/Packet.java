package fr.snake.multiplayer.packet;

import fr.snake.multiplayer.packet.client.JoinPacket;
import fr.snake.multiplayer.packet.server.ServerSettingsPacket;
import fr.snake.multiplayer.packet.server.SetFoodPacket;
import fr.snake.multiplayer.packet.server.SnakeRemovedPacket;
import fr.snake.multiplayer.packet.server.SnakeSpawnedPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {
	private final PacketType type;

	public Packet(PacketType type) {
		this.type = type ;
	}

	public static Packet newPacket(PacketType type) {
		switch (type) {
			case SERVER_SETTINGS:
				return new ServerSettingsPacket() ;
			case JOIN:
				return new JoinPacket() ;
			case SNAKE_SPAWNED:
				return new SnakeSpawnedPacket() ;
			case SNAKE_REMOVED:
				return new SnakeRemovedPacket() ;
			case CHANGE_SNAKE:
				return new ChangeSnakePacket() ;
			case SET_FOOD:
				return new SetFoodPacket() ;
			default:
				throw new IllegalArgumentException() ;
		}
	}

	public static void write(DataOutputStream dos, Packet packet) throws IOException {
		dos.writeInt(packet.type.ordinal()) ;
		packet.writePacket(dos) ;
		dos.flush() ;
	}

	public static Packet read(DataInputStream dis) throws IOException {
		int typeOrdinal = dis.readInt() ;
		PacketType type = PacketType.values()[typeOrdinal] ;
		Packet packet = Packet.newPacket(type) ;
		packet.readPacket(dis) ;
		return packet ;
	}

	/**
	 * Écrire les d onnées du paquet.
	 * @param dos data output stream
	 * @throws IOException
	 */
	protected abstract void writePacket(DataOutputStream dos) throws IOException ;

	/**
	 * Lis les données du pa quet et les placent dans les ch amps de l'objet.
	 * @param dis data input stream
	 * @throws IOException
	 */
	protected abstract void readPacket(DataInputStream dis) throws IOException ;

	public PacketType getType() {
		return type ;
	}
}
