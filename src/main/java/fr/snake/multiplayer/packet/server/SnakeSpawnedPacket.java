package fr.snake.multiplayer.packet.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;

import fr.snake.snakes.Snake;
import fr.snake.segments.Segment;
import fr.snake.segments.SegmentSerializer;
import fr.snake.snakes.SnakeAutonome;
import fr.snake.snakes.SnakeListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedList;

public class SnakeSpawnedPacket extends Packet {
	private Snake snake ;
	private boolean isPlayer ;
	public SnakeSpawnedPacket() {
		super(PacketType.SNAKE_SPAWNED) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Segment.class, new SegmentSerializer())
				.create() ;
		synchronized (snake) {
			String json = gson.toJson(snake) ;
			dos.writeBoolean(snake instanceof SnakeAutonome) ;
			dos.writeBoolean(isPlayer) ;
			dos.writeUTF(json) ;
		}
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Segment.class, new SegmentSerializer())
				.create() ;
		boolean isAi = dis.readBoolean() ;
		this.isPlayer = dis.readBoolean() ;
		String json = dis.readUTF() ;
		if (isAi) {
			snake = gson.fromJson(json, SnakeAutonome.class) ;
		} else {
			snake = gson.fromJson(json, Snake.class) ;
		}
		// Par d éfaut, GSON ne peut ni sérialiser ni dé sérialiser c orrectement ArrayList <SnakeListener> (et
		// ne devrait pas, car ce n'est pas à partager entre client et serveur), il faut donc ma nuellement le
		// re créer pour qu'il ne soit pas null.
		snake.setSnakeListeners(new ArrayList<>()) ;
		snake.setLastPositions(new LinkedList<>()) ;
	}

	public Snake getSnake() {
		return snake ;
	}

	public void setSnake(Snake snake) {
		this.snake = snake ;
	}

	public boolean isPlayer() {
		return isPlayer ;
	}

	public void setIsPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer ;
	}
}
