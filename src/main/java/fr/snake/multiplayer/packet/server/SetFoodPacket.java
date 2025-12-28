package fr.snake.multiplayer.packet.server;

import com.google.gson.Gson;
import fr.snake.Food;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Change la no urriture présente sur la carte
 */
public class SetFoodPacket extends Packet {
	private Food[] foods;

	public SetFoodPacket() {
		super(PacketType.SET_FOOD) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		Gson gson = new Gson() ;
		String json = gson.toJson(foods) ;
		dos.writeUTF(json) ;
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		Gson gson = new Gson() ;
		String json = dis.readUTF() ;
		foods = gson.fromJson(json, Food[].class) ;
	}

	public Food[] getFoods() {
		return foods ;
	}

	public void setFoods(Food[] foods) {
		this.foods = foods ;
	}

}
