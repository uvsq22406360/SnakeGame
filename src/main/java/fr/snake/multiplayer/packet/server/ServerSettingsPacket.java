package fr.snake.multiplayer.packet.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;
import fr.snake.Settings;
import fr.snake.multiplayer.packet.Packet;
import fr.snake.multiplayer.packet.PacketType;

/**
 * Paquet envoyé par le serveur et con tenant les pa ramètres du jeu à prendre
 */
public class ServerSettingsPacket extends Packet {
	private Settings settings = null;

	public ServerSettingsPacket() {
		super(PacketType.SERVER_SETTINGS) ;
	}

	@Override
	protected void writePacket(DataOutputStream dos) throws IOException {
		Gson gson = new Gson();
		String json = gson.toJson(settings) ;
		dos.writeUTF(json) ;
	}

	@Override
	protected void readPacket(DataInputStream dis) throws IOException {
		Gson gson = new Gson() ;
		String json = dis.readUTF() ;
		settings = gson.fromJson(json, Settings.class) ;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
