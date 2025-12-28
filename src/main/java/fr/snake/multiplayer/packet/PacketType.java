package fr.snake.multiplayer.packet;

import fr.snake.multiplayer.Server;

public enum PacketType {
	// --- Envoyé par le serveur ---

	/**
	 * Les paramètres du serveur sont envoyés.
	 */
	SERVER_SETTINGS,
	/**
	 * Un serpent est apparu.
	 */
	SNAKE_SPAWNED,
	/**
	 * Un serpent est enlevé de la partie (soit il est mort, soit le joueur a quitté).
	 */
	SNAKE_REMOVED,
	/**
	 * Change la nourriture présente sur la carte
	 */
	SET_FOOD,

	// --- Envoyé par le client ---

	JOIN,

	// --- Envoyé par les deux ---

	/**
	 * Changer les propriétés du serpent (dont la direction et les segments)
	 */
	CHANGE_SNAKE,
	;
}
