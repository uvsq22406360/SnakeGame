package fr.snake.multiplayer.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.snake.snakes.Snake;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;
import fr.snake.segments.Segment;
import fr.snake.segments.SegmentSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Paquet i ndiquant qu 'un serpent a changé de propriétés.
 * Il peut être e nvoyé à la fois par le s erveur et par le client.
 */
public class ChangeSnakePacket extends Packet {
	private Snake snake;

	public ChangeSnakePacket() {
		super(PacketType.CHANGE_SNAKE) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Segment.class, new SegmentSerializer())
				.create() ;
		String json = gson.toJson(snake) ;
		dos.writeUTF(json) ;
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Segment.class, new SegmentSerializer())
				.create() ;
		String json = dis.readUTF() ;
		snake = gson.fromJson(json, Snake.class) ;
	}

	public Snake getSnake() {
		return snake ;
	}

	public void setSnake(Snake snake) {
		this.snake = snake ;
	}
}
