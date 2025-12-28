package fr.snake;

import java.io.Serializable;
import java.util.Random;

/**
 * R eprésente de la n ourriture présente sur la carte. Ainsi, cette classe a pour propriétés la position, la taille
 * et le type de nourriture. Les instances sont immuables, car dans le jeu la n ourriture n'est pas modifiée, elle ne peut
 * qu 'apparaitre ou être mangée.
 */
public class Food implements Serializable {
	private static final long serialVersionUID = 182049338994836767L;
	private int x;
	private int y;
	private int size;
	private final Type type;
	
	public Food(int x, int y, int size, Type type) {
		super() ;
		this.x = x ;
		this.y = y ;
		this.size = size ;
		this.type = type ;
	}

	/**
	 * Renvoie la position X
	 * @return x
	 */
	public int getX() {
		return x ;
	}

	/**
	 * Renvoie la position Y
	 * @return y
	 */
	public int getY() {
		return y ;
	}

	/**
	 * Renvoie la taille de la nourriture
	 * @return taille
	 */
	public int getSize() {
		return size ;
	}

	public Type getType() {
		return type ;
	}

	public enum Type {
		/**
		 * Type de nourriture par défaut, qui ne fait que rallonger le serpent
		 */
		DEFAULT,
		/**
		 * Crée un segment qui au lieu de causer la mort du serpent qui rentre dedans, casse la queue du serpent à ce niveau
		 */
		WEAK,
		/**
		 * Crée un segment qui évite une mort immédiate lors d'une collision, s'il est en tete.
		 */
		SHIELD,
		/**
		 * Nourriture empoisonnée, qui tue le serpent qui en mange.
		 */
		POISONED,
		/**
		 * Accélère ou ralentit la vitesse du serpent
		 */
		AERODYNAMIC ;

		public static Type randomFoodType(Random r) {
			Type[] values = Type.values() ;
			int index = r.nextInt(values.length) ;
			return values[index] ;
		}

		public static Type randomNonPoisonedFoodType(Random r) {
			Type[] values = new Type[] { DEFAULT, WEAK, SHIELD, AERODYNAMIC } ;
			int index = r.nextInt(values.length) ;
			return values[index] ;
		}
	}
}
