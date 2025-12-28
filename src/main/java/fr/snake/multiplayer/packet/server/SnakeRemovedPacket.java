package fr.snake.multiplayer.packet.server;

import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SnakeRemovedPacket extends Packet {
	private UUID uuid;

	public SnakeRemovedPacket() {
		super(PacketType.SNAKE_REMOVED) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		dos.writeUTF(uuid.toString()) ;
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		uuid = UUID.fromString(dis.readUTF()) ;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
