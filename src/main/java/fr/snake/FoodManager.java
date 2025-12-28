package fr.snake;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Ge stionnaire de la nourriture. Cette classe s'occupe de l'intégralité de la n ourriture présente sur la carte,
 * de son agencement, de l'apparition de nouvelle nourriture, etc.
 */
public class FoodManager extends Observable implements Sprite, Serializable {
	private static final long serialVersionUID = 1872523945393095171L ;
	private final Random r = new Random() ;
	private final List<Food> foods = new ArrayList<Food>() ;
	
	private FoodManager() { }

	/**
	 * Fabrique statique de Food Manager
	 * @param settings Les paramètres à prendre en compte
	 * @return une instance de <code>FoodManager</code>
	 */
	public static FoodManager createFoodManager(Settings settings) {
		FoodManager foodManager = new FoodManager() ;
		for (int i = 0; i < settings.getMinimumFoods(); i++) {
			foodManager.createFood(settings) ;
		}
		return foodManager;
	}

	/**
	 * Renvoie un tableau immuable de la no urriture présente sur la carte du jeu.
	 * Cette méthode renvoie un tableau au lieu d'une List<Food> afin de forcer
	 * par le typage le fait de devoir utiliser les méthodes <code>createFood()</code> et <code>removeFood()</code>
	 * @return tableau de la nourriture
	 */
	public Food[] getFoods() {
		return foods.toArray(new Food[0]) ;
	}

	/**
	 * Change l 'intégralité de la n ourriture présente sur la carte par celle contenue dans le tableau d onné.
	 * @param foods La nouvelle nourriture
	 */
	public void setFoods(Food[] foods) {
		this.foods.clear() ;
		for (Food food : foods) {
			this.foods.add(food) ;
		}
	}

	/**
	 * Crée de la  nourriture d'un type au h asard et le place au hasard sur la carte
	 * @param settings Les paramètres à prendre en compte
	 */
	public void createFood(Settings settings) {
		int x = r.nextInt(settings.getWidth()) ;
		int y = r.nextInt(settings.getHeight()) ;
		Food.Type type = Food.Type.randomFoodType(r) ;
		createFood(settings, x, y, type) ;
	}

	/**
	 * Crée de la no urriture d'un type donné et à un endroit donné, mais d'une taille choisie au hasard sur la carte.
	 * @param settings Les paramètres à prendre en compte
	 * @param x La position X
	 * @param y La position Y
	 * @param type Le type de la nourriture
	 */
	public void createFood(Settings settings, int x, int y, Food.Type type) {
		int size = r.nextInt(settings.getDefaultBodyWidth() / 2) + 8;
		foods.add(new Food(x, y, size, type)) ;

		setChanged() ;
		notifyObservers() ;
	}

	/**
	 * Retire une nourriture de la carte
	 * @param food nourriture à retirer
	 */
	public void removeFood(Food food) {
		foods.remove(food) ;
		setChanged() ;
		notifyObservers() ;
	}

	/**
	 * Affiche la nourriture présente sur la carte.
	 * @param g L'objet <code>Graphics2D</code> pour dessiner le sprite
	 * @param settings Les paramètres à prendre en compte
	 * @param dx Le défilement horizontal
	 * @param dy Le défilement vertical
	 */
	@Override
	public void paint(Graphics2D g, Settings settings, int dx, int dy) {
		for (int i = 0; i < foods.size(); i++) {
			Food food = foods.get(i);
			switch (food.getType()) {
				case DEFAULT:
					g.setColor(Color.RED) ;
					break;
				case WEAK:
					g.setColor(Color.WHITE) ;
					break;
				case SHIELD:
					g.setColor(Color.GRAY) ;
					break;
				case POISONED:
					g.setColor(Color.GREEN) ; 
					break;
				case AERODYNAMIC:
					g.setColor(Color.BLUE) ;
					break;
			}

			// La n ourriture est dessinée en plusieurs fois à chaque r épétition du terrain afin de donner l'illusion
			// d'un terrain infini
			for (int x = -settings.getWidth(); x <= settings.getWidth(); x += settings.getWidth()) {
				for (int y = -settings.getHeight(); y <= settings.getHeight(); y += settings.getHeight()) {
					int ax = food.getX()+food.getSize()/2 - dx + x ;
					int ay = food.getY()+food.getSize()/2 - dy + y ;
					// Test d 'occlusion, si la nourriture est en dehors de la f enêtre, pas besoin de surcharger le GPU
					// en faisant fillOval.
					if (ax >= 0 && ay >= 0 && ax <= settings.getWindowWidth() && ay <= settings.getWindowHeight()) {
						g.fillOval(ax, ay, food.getSize() / 2, food.getSize() / 2) ; 
					}
				}
			}
		}
	}
}
