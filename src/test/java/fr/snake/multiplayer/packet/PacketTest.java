package fr.snake.multiplayer.packet;

import fr.snake.multiplayer.packet.client.JoinPacket;
import fr.snake.multiplayer.packet.server.ServerSettingsPacket;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PacketTest {

	@Test
	public void newPacket() {
		Packet packet = Packet.newPacket(PacketType.SERVER_SETTINGS) ;
		assertTrue(packet instanceof ServerSettingsPacket) ;

		Packet packet2 = Packet.newPacket(PacketType.JOIN) ;
		assertTrue(packet2 instanceof JoinPacket) ;
	}

	@Test
	void getType() {
		assertTrue(Packet.newPacket(PacketType.JOIN).getType() == PacketType.JOIN) ;
		assertTrue(Packet.newPacket(PacketType.SNAKE_REMOVED).getType() == PacketType.SNAKE_REMOVED) ;

		for (PacketType type : PacketType.values()) {
			assertTrue(Packet.newPacket(type).getType() == type) ;
		}
	}
}
