package fr.snake;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Les par amètres du jeu
 */
public class Settings {
	private int width = 600 * 4 ;
	private int height = 520 * 4 ;
	private int windowWidth = 800 ;
	private int windowHeight = 600 ;
	private int defaultSnakeLength = 5 ;
	private int defaultBodyWidth = 40 ;
	private int minimumFoods = 20 ;
	private int maximumFoods = 150 ;
	/**
	 * Nombre mi nimum de serpents. Le jeu rajoute des IA s'il n'y en a pas assez.
	 */
	private int minimumSnakes = 3 ;
	private Skin defaultSkin = Skin.SKIN_1 ;
	private Background.Map defaultMap = Background.Map.MAP_1 ;
	/**
	 * Est-ce que le joueur a reçu des explications du gameplay
	 */
	private boolean explained = false ;

	public Settings() {}

	public static Settings fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Settings.class) ;
	}

	public static Settings fromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file) ;
		byte[] bytes = fis.readAllBytes() ;
		String str = new String(bytes, "UTF-8") ;
		fis.close();
		return fromJson(str) ;
	}

	public String toJson() {
		Gson gson = new Gson() ;
		return gson.toJson(this) ;
	}

	public void save(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file) ;
		fos.write(toJson().getBytes()) ;
		fos.close() ;
	}

	public final static String BGM_URL="/Sounds/bg.wav" ;
	public final static String EAT_URL="/Sounds/bekilled.wav" ;

	public int getWidth() {
		return width ;
	}

	public void setWidth(int width) {
		this.width = width ;
	}

	public int getHeight() {
		return height ;
	}

	public void setHeight(int height) {
		this.height = height ;
	}

	public int getWindowWidth() {
		return windowWidth ;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth ;
	}

	public int getWindowHeight() {
		return windowHeight ;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight ;
	}

	public int getDefaultSnakeLength() {
		return defaultSnakeLength ;
	}

	public void setDefaultSnakeLength(int defaultSnakeLength) {
		this.defaultSnakeLength = defaultSnakeLength ;
	}

	public int getDefaultBodyWidth() {
		return defaultBodyWidth ;
	}

	public void setDefaultBodyWidth(int defaultBodyWidth) {
		this.defaultBodyWidth = defaultBodyWidth ;
	}

	public int getMinimumFoods() {
		return minimumFoods ;
	}

	public void setMinimumFoods(int minimumFoods) {
		this.minimumFoods = minimumFoods ;
	}

	public int getMaximumFoods() {
		return maximumFoods ;
	}

	public void setMaximumFoods(int maximumFoods) {
		this.maximumFoods = maximumFoods ;
	}

	public int getMinimumSnakes() {
		return minimumSnakes ;
	}

	public void setMinimumSnakes(int minimumSnakes) {
		this.minimumSnakes = minimumSnakes ;
	}

	public Skin getDefaultSkin() {
		return defaultSkin ;
	}

	public void setDefaultSkin(Skin defaultSkin) {
		this.defaultSkin = defaultSkin ;
	}

	public Background.Map getDefaultMap() {
		return defaultMap ;
	}

	public void setDefaultMap(Background.Map defaultMap) {
		this.defaultMap = defaultMap ;
	}

	public boolean isExplained() {
		return explained ;
	}

	public void setExplained(boolean explained) {
		this.explained = explained ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true ;
		if (o == null || getClass() != o.getClass()) return false ;
		Settings settings = (Settings) o ;
		return width == settings.width && height == settings.height && windowWidth == settings.windowWidth && windowHeight == settings.windowHeight && defaultSnakeLength == settings.defaultSnakeLength && defaultBodyWidth == settings.defaultBodyWidth && minimumFoods == settings.minimumFoods && maximumFoods == settings.maximumFoods && minimumSnakes == settings.minimumSnakes && defaultSkin == settings.defaultSkin && defaultMap == settings.defaultMap && explained == settings.explained ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height, windowWidth, windowHeight, defaultSnakeLength, defaultBodyWidth, minimumFoods, maximumFoods, minimumSnakes, defaultSkin, defaultMap, explained) ;
	}
}
