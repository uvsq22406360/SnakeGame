package fr.snake.states;

/**
 * Une interface qui pe rmet de changer l'état du jeu.
 */
public interface StateChanger {
	/**
	 * Change l 'état du jeu vers ce nouvel état.
	 * @param newState nouvel état
	 */
	public void setState(State newState) ;
}
