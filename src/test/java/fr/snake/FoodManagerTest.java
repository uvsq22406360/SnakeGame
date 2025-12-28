package fr.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FoodManagerTest {

	@Test
	public void createFoodManager() {
		Settings settings = new Settings() ;
		FoodManager foodManager = FoodManager.createFoodManager(settings) ;
		assertEquals(settings.getMinimumFoods(), foodManager.getFoods().length) ;
	}

	@Test
	public void createFood() {
		Settings settings = new Settings();
		FoodManager foodManager = FoodManager.createFoodManager(settings) ;
		int oldLength = foodManager.getFoods().length ;

		foodManager.createFood(settings);

		int newLength = foodManager.getFoods().length ;
		assertEquals(oldLength + 1, newLength) ;
	}

	@Test
	public void testCreateFood() {
		Settings settings = new Settings() ;
		FoodManager foodManager = FoodManager.createFoodManager(settings) ;
		int oldLength = foodManager.getFoods().length;

		foodManager.createFood(settings, 0, 0, Food.Type.WEAK) ;
		boolean exists = false;
		for (Food food : foodManager.getFoods()) {
			if (food.getX() == 0 && food.getY() == 0 && food.getType() == Food.Type.WEAK) {
				exists = true;
			}
		}
		assertTrue(exists) ;
	}

	@Test
	void removeFood() {
		Settings settings = new Settings() ;
		FoodManager foodManager = FoodManager.createFoodManager(settings) ;
		int oldLength = foodManager.getFoods().length;

		foodManager.removeFood(foodManager.getFoods()[0]) ;

		int newLength = foodManager.getFoods().length;
		assertEquals(oldLength - 1, newLength) ;
	}
}