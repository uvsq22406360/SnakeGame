package fr.snake.multiplayer.packet.client;

import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinPacket extends Packet {
	private String username ;

	public JoinPacket() {
		super(PacketType.JOIN) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		dos.writeUTF(username) ;
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		username = dis.readUTF() ;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username ;
	}
}
