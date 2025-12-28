package fr.snake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

	@Test
	void fromJson() {
		Settings settings = Settings.fromJson(
				"{\"width\":2400,\"height\":2080,\"windowWidth\":1086,\"windowHeight\":715,\"defaultSnakeLength\":5,\"defaultBodyWidth\":40,\"minimumFoods\":20,\"maximumFoods\":150,\"minimumSnakes\":3,\"defaultSkin\":\"SKIN_4\",\"defaultMap\":\"MAP_1\",\"explained\":true}"
		);
		assertEquals(2400, settings.getWidth()) ;
		assertEquals(2080, settings.getHeight()) ;
		assertEquals(1086, settings.getWindowWidth()) ;
		assertEquals(715, settings.getWindowHeight()) ;
		assertEquals(5, settings.getDefaultSnakeLength()) ;
		assertEquals(40, settings.getDefaultBodyWidth()) ;
		assertEquals(20, settings.getMinimumFoods()) ;
		assertEquals(150, settings.getMaximumFoods()) ;
		assertEquals(3, settings.getMinimumSnakes()) ;
		assertEquals(Skin.SKIN_4, settings.getDefaultSkin()) ;
		assertEquals(Background.Map.MAP_1, settings.getDefaultMap()) ;
		assertEquals(true, settings.isExplained()) ;
	}

	@Test
	void toJson() {
		Settings settings = new Settings() ;
		settings.setWidth(400) ;
		settings.setHeight(400) ;
		settings.setWindowWidth(400) ;
		settings.setWindowHeight(400) ;
		settings.setDefaultSnakeLength(400) ;
		settings.setDefaultBodyWidth(400) ;
		settings.setMinimumFoods(400) ;
		settings.setMaximumFoods(400) ;
		settings.setMinimumSnakes(400) ;
		settings.setDefaultSkin(Skin.SKIN_1) ;
		settings.setDefaultMap(Background.Map.MAP_1) ;
		settings.setExplained(false) ;
		assertEquals("{\"width\":400,\"height\":400,\"windowWidth\":400,\"windowHeight\":400,\"defaultSnakeLength\":400,\"defaultBodyWidth\":400,\"minimumFoods\":400,\"maximumFoods\":400,\"minimumSnakes\":400,\"defaultSkin\":\"SKIN_1\",\"defaultMap\":\"MAP_1\",\"explained\":false}",
				settings.toJson()) ;
		Settings newSettings = Settings.fromJson(settings.toJson()) ;
		assertEquals(settings, newSettings) ;
	}

	@Test
	void width() {
		Settings settings = new Settings() ;
		settings.setWidth(400);
		assertEquals(400, settings.getWidth()) ;
	}

	@Test
	void height() {
		Settings settings = new Settings() ;
		settings.setHeight(400) ;
		assertEquals(400, settings.getHeight()) ;
	}

	@Test
	void windowWidth() {
		Settings settings = new Settings() ;
		settings.setWindowWidth(400) ;
		assertEquals(400, settings.getWindowWidth()) ;
	}

	@Test
	void windowHeight() {
		Settings settings = new Settings() ;
		settings.setWindowHeight(400) ;
		assertEquals(400, settings.getWindowHeight()) ;
	}

	@Test
	void defaultSnakeLength() {
		Settings settings = new Settings() ;
		settings.setDefaultSnakeLength(400) ;
		assertEquals(400, settings.getDefaultSnakeLength()) ;
	}

	@Test
	void defaultBodyWidth() {
		Settings settings = new Settings() ;
		settings.setDefaultBodyWidth(400) ;
		assertEquals(400, settings.getDefaultBodyWidth()) ;
	}

	@Test
	void minimumFoods() {
		Settings settings = new Settings() ;
		settings.setMinimumFoods(400) ;
		assertEquals(400, settings.getMinimumFoods()) ;
	}

	@Test
	void maximumFoods() {
		Settings settings = new Settings() ;
		settings.setMaximumFoods(400) ;
		assertEquals(400, settings.getMaximumFoods()) ;
	}

	@Test
	void minimumSnakes() {
		Settings settings = new Settings() ;
		settings.setMinimumSnakes(400) ;
		assertEquals(400, settings.getMinimumSnakes()) ;
	}

	@Test
	void defaultSkin() {
		Settings settings = new Settings() ;
		settings.setDefaultSkin(Skin.SKIN_1) ;
		assertEquals(Skin.SKIN_1, settings.getDefaultSkin()) ;
	}

	@Test
	void defaultMap() {
		Settings settings = new Settings() ;
		settings.setDefaultMap(Background.Map.MAP_1) ;
		assertEquals(Background.Map.MAP_1, settings.getDefaultMap()) ;
	}

	@Test
	void explained() {
		Settings settings = new Settings() ;
		settings.setExplained(true) ;
		assertTrue(settings.isExplained()) ;
	}
}