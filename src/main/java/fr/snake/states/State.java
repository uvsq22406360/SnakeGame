package fr.snake.states;

import java.awt.*;

public abstract class State {
	public abstract void update() ;
	public abstract void paint(Graphics g) ;
	public void onMouseMove(int mouseX, int mouseY) {}
	public void onMouseClick(int mouseX, int mouseY) {}
	public void onWindowResize(int width, int height) {}
	public void onKeyPressed(int code) {}
	public void onKeyReleased(int code) {}
}
