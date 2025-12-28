package fr.snake;

import org.junit.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FoodTest {

	@Test
	public void randomFoodType() {
		Random r = new Random() ;
		for (int i = 0; i < 30; i++) {
			Food.Type type = Food.Type.randomFoodType(r) ;
		}
	}

	@Test
	public void randomNonPoisonedFoodType() {
		Random r = new Random() ;
		for (int i = 0; i < 30; i++) {
			Food.Type type = Food.Type.randomNonPoisonedFoodType(r) ;
			assertNotEquals(Food.Type.POISONED, type) ;
		}
	}

}