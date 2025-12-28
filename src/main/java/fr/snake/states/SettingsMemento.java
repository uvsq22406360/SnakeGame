package fr.snake.states;

import fr.snake.Settings;

public class SettingsMemento {
	private Settings settings ;

	public SettingsMemento(Settings settings) {
		this.settings = settings;
	}

	public Settings getSettings() {
		return settings ;
	}
}
