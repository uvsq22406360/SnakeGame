package fr.snake;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BackgroundTest {

	@Test
	public void map1() {
		new Background(Background.Map.MAP_1) ;
	}

	@Test
	public void map2() {
		new Background(Background.Map.MAP_2) ;
	}

}