package fr.snake.snakes;

import fr.snake.*;
import fr.snake.segments.DefaultSegment;
import fr.snake.segments.Segment;
import fr.snake.segments.ShieldSegment;
import fr.snake.segments.WeakSegment;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Snake implements Sprite, Serializable {
	private static final long serialVersionUID = -8894654319523582919L;
	private LinkedList<Segment> body;
	private boolean isAlive;
	private double bodyWidth;
	private double speed;
	/**
	 * Le boost de vitesse donné par la nourriture aé rodynamique
	 */
	private double speedBoost;
	private Skin skin;
	/**
	 * Le nom d 'u tilisateur du serpent
	 */
	private String username = "";
	/**
	 * Un i dentifiant qui distingue de manière unique le serpent des autres.
	 * Il est p rincipalement utilisé pour le multijoueur.
	 */
	private final UUID uuid;
	private transient ArrayList<SnakeListener> snakeListeners;
	private transient LinkedList<Point2D.Double> lastPositions;

	protected Snake(UUID uuid, int startX, int startY, int taille, int bodyWidth, Skin skin) {
		this.uuid = uuid;
		this.isAlive = true;
		this.bodyWidth = bodyWidth;
		this.body = new LinkedList<>() ;
		this.skin = skin;
		this.snakeListeners = new ArrayList<>() ;
		this.lastPositions = new LinkedList<>() ;

		for (int i = 0; i < taille; i++) {
			body.add(new DefaultSegment(startX, startY+i*10, -90)) ;
		}
		this.recomputeSpeed() ;
	}

	/**
	 * Fa brique statique de la classe Snake, qui lit les paramètres et renvoie un serpent attribué au joueur (sans IA).
	 * @param settings Les paramètres à prendre en compte
	 * @return un serpent du joueur
	 */
	public static Snake createPlayerSnake(Settings settings, String username) {
		Random r = new Random() ;
		UUID uuid = UUID.randomUUID() ;
		int startX = r.nextInt(settings.getWidth()) ;
		int startY = r.nextInt(settings.getHeight()) ;
		Snake snake = new Snake(uuid, startX, startY, settings.getDefaultSnakeLength(), settings.getDefaultBodyWidth(), settings.getDefaultSkin()) ;
		snake.setUsername(username) ;
		return snake;
	}

	/**
	 * Fabrique statique de la classe SnakeAutonome, qui lit les paramètres et re nvoie un serpent avec IA
	 * @param settings Les p aramètres à prendre en compte
	 * @return un serpent avec IA
	 */
	public static Snake createAISnake(Settings settings) {
		Random r = new Random() ;
		UUID uuid = UUID.randomUUID() ;
		int startX = r.nextInt(settings.getWidth()) ;
		int startY = r.nextInt(settings.getHeight()) ;
		Skin skin = Skin.randomSkin(r) ;
		Snake snake = new SnakeAutonome(uuid, startX, startY, settings.getDefaultSnakeLength(), settings.getDefaultBodyWidth(), settings.getDefaultSkin()) ;
		snake.setUsername("IA") ;
		snake.setSkin(skin) ;
		return snake;
	}

	public void setDirection(double x, double y) {
		Segment head = body.peekFirst() ;
		assert head != null;
		head.setAngle(angleTo(x, y)) ;
		for (SnakeListener listener : snakeListeners) {
			listener.snakeSetDirection(this, head.getAngle()) ;
		}
	}

	public double angleTo(double x, double y) {
		Segment head = body.peekFirst() ;
		assert head != null;
		double hx = head.getX()+bodyWidth/2;
		double hy = head.getY()+bodyWidth/2;
		// Les co ordonnées (x, y) données correspondent à celle de la tete, ce qui n'est pas valide.
		// if (x == hx && y == hy) throw new IllegalArgumentException();
		if (x == hx && y == hy) {
			return 0;
		}

		double x1 = x - hx;
		double y1 = y - hy;
		return Math.atan2(y1,x1) ;
	}

	/**
	 * Copie les pr opriétés de l'autre serpent sauf pour
	 * L'UUID et bodyWidth car ils sont finals donc ne sont pas changés.
	 * @param other l'autre serpent
	 */
	public void copy(Snake other) {
		body = other.body;
		isAlive = other.isAlive;
		speed = other.speed;
		skin = other.skin;
		bodyWidth = other.bodyWidth;
	}

	public Snake clone() {
		Snake clone = new Snake(uuid, 0, 0, 0, 10, skin) ;
		clone.copy(this) ;
		return clone;
	}

	/**
	 * La fonction move fait avancer le snake times fois, donc en déplace la position de la queue au debut de la liste
	 * en copiant les co ordonnées et la direction de la tete donnant l 'illusion que le serpent "rampe" vers l'avant
	 * @param settings Les par amètres à prendre en compte
	 */
	public void move(Settings settings) {
		// L'espacement, en pixels, entre chaque segment
		int spacing = 10;

		for (int step = 0; step < Math.round(speed); step++) {
			Segment[] segments = body.toArray(new Segment[0]) ;
			// Délai de 10 trames
			for (int i = segments.length - 1; i >= 1; i--) {
				int index = (segments.length - i) * spacing;
				if (lastPositions.size() > index) {
					Point2D.Double lastPos = lastPositions.get(index) ;
					segments[i].setX(lastPos.getX()) ;
					segments[i].setY(lastPos.getY()) ;
					segments[i].setAngle(segments[i - 1].getAngle()) ;
				}
			}

			Segment head = body.peekFirst();
			assert head != null;
			double newX = head.getX() + Math.cos(head.getAngle()) ;
			if (newX < 0) {
				head.setX(settings.getWidth() + newX) ;
			} else {
				head.setX(newX % settings.getWidth()) ;
			}

			double newY = head.getY() + Math.sin(head.getAngle()) ;
			if (newY < 0) {
				head.setY(settings.getHeight() + newY) ;
			} else {
				head.setY(newY % settings.getHeight()) ;
			}

			synchronized (this) {
				lastPositions.add(new Point2D.Double(head.getX(), head.getY())) ;
				while (lastPositions.size() > segments.length * spacing) {
					lastPositions.pollFirst() ;
				}
			}
		}

		Segment head = body.peekFirst() ;
		for (SnakeListener listener : snakeListeners) {
			listener.snakeHeadMoved(this, head.getX(), head.getY()) ;
		}
	}

	/**
	 * Vérifie les co llisions avec les autres serpents, et tue le serpent si on est rentré dedans.
	 * Cette méthode doit être appelée juste après move() pour éviter d'être tué si un autre serpent nous est
	 * rentré dedans.
	 * @param settings Les paramètres à prendre en compte
	 * @param snakes La liste des serpents présents dans le jeu
	 */
	public void checkCollisions(Settings settings, List<Snake> snakes, FoodManager foodManager) {
		for (Snake snake : snakes) {
			// On ne va pas vérifier les collisions avec nous meme
			// De plus, sur slither.io, un serpent peut se che vaucher lui-meme ce qui est aussi un élément du gameplay
			if (snake != this && snake.isAlive()) {
				// On vérifie la c ollision entre la tete de ce serpent et les segments des autres serpents
				Segment head = body.getFirst() ;
				for (Segment otherSegment : snake.getBody()) {
					// Pour savoir si deux sphères rentrent en collision, il faut calculer la distance entre
					// leur centre. Si elle est inférieure au diamètre, alors il y a col lision.
					double distance = Math.sqrt(
							// On pourrai t faire (head.getX() + bodyWidth/2) - (otherSegment.getX() + bodyWidth/2)
							// pour bien faire la distance entre les centres, mais si on regarde, on peut voir que
							// les bodyWidth/2 s'annulent, donc il n'y a pas besoin de les mettre.
							Math.pow(head.getX() - otherSegment.getX(), 2) +
							Math.pow(head.getY() - otherSegment.getY(), 2)
					);
					if (distance < bodyWidth) {
						if (otherSegment.causesCollision()) {
							head.onCollision(this, otherSegment, settings, foodManager) ;
						}
						otherSegment.onCollided(snake, head, settings, foodManager) ;
						this.speedBoost = 0;
						this.speed = 0;
						return;
					}
				}
			}
		}
	}

	/**
	 * Déplace le serpent et vérifie les collisions
	 * @param settings Les pa ramètres à prendre en compte
	 * @param snakes La liste des serpents p résents dans le jeu
	 */
	public void moveAndCheckCollisions(Settings settings, List<Snake> snakes, FoodManager foodManager) {
		move(settings);
		checkCollisions(settings, snakes, foodManager) ;
	}

	public void updateAi(Settings settings, FoodManager food, List<Snake> snakes) {
		// Pour les serpents joueurs, cette méthode ne fait rien.
	}

	/**
	 * Fais mourir le serpent et fais ap paraitre de la nourriture là où il était.
	 * @param settings
	 */
	public void die(Settings settings, FoodManager foodManager) {
		if (!isAlive()) return;
		setAlive(false) ;

		for (int i = 0; i < body.size(); i++) {
			Segment segment = body.get(i) ;
			if (i % 2 == 0)
				foodManager.createFood(settings, (int) segment.getX(), (int) segment.getY(), Food.Type.randomNonPoisonedFoodType(new Random())) ;
		}
	}
	
	public void eat(Settings settings, FoodManager foodManager) {
		// Le speed boost se réduit petit à petit
		this.speedBoost = this.speedBoost * 0.9 + 0 * 0.1;
		this.recomputeSpeed() ;

		Segment head = body.peekFirst();
		Food[] foods = foodManager.getFoods() ;
		double headR = bodyWidth/2;
		assert head != null;
		double headX = head.getX()+headR;
		double headY = head.getY()+headR;
		for (int i = 0; i < foods.length; i++) {
			Food food = foods[i];
			int foodR = food.getSize()/2;
			int foodX = food.getX()+foodR;
			int foodY = food.getY()+foodR;
			double aX = Math.pow(headX-foodX, 2) ;
			double aY = Math.pow(headY-foodY, 2) ;
			double aR = Math.pow(headR+foodR, 2) ;
			if(aX+aY<=aR){
				//Music.play(Config.EAT_URL, false) ;
				Segment tail = body.peekLast();
				int foodSize = food.getSize()/5;
				while(foodSize>0){
					synchronized (body) {
						Segment segment = switch (food.getType()) {
							case WEAK -> new WeakSegment(head.getX(), head.getY(), tail.getAngle()) ;
							case SHIELD -> new ShieldSegment(head.getX(), head.getY(), tail.getAngle()) ;
							case POISONED -> {
								die(settings, foodManager) ;
								yield null;
							}
							case AERODYNAMIC -> {
								this.speedBoost += 2.0;
								yield null;
							}
							default -> new DefaultSegment(head.getX(), head.getY(), tail.getAngle()) ;
						};
						if (segment != null) {
							if (food.getType() == Food.Type.SHIELD) {
								if (!(body.getFirst() instanceof ShieldSegment)) // il ne peut pas y avoir 2 segments boucliers
									body.addFirst(segment) ;
							} else {
								body.addLast(segment) ;
							}
						}
					}
					foodSize--;
				}
				foodManager.removeFood(food) ;
			}
		}
	}

	/**
	 * Recalcule la vitesse du serpent
	 */
	private void recomputeSpeed() {
		int length = this.body.size() ;
		double target = 7 + length * 0.1 + speedBoost;
		this.speed = 0.1 * target + 0.9 * this.speed; // in terpolation linéaire de la vitesse
	}

	/**
	 * Brule un segment pour a ccélérer
	 */
	public void burnSegment(Settings settings) {
		if (this.body.size() > settings.getDefaultSnakeLength() && this.speedBoost < 15) {
			this.body.pollLast() ;
			this.speedBoost += 10;
		}
	}

	@Override
	public void paint(Graphics2D g, Settings settings, int dx, int dy) {
		AffineTransform trans = new AffineTransform() ;
		// Le serpent est dessiné en plusieurs fois à chaque répétition du terrain afin de donner l'illusion
		// d'un terrain infini
		for (int x = -settings.getWidth(); x <= settings.getWidth(); x += settings.getWidth()) {
			for (int y = -settings.getHeight(); y <= settings.getHeight(); y += settings.getHeight()) {
				for (int i = body.size() - 1; i >= 0; i--) {
					Segment segment = body.get(i) ;

					BufferedImage img;
					if (i == 0) {
						img = skin.getHeadImage() ;
					} else if (i == body.size() - 1) {
						img = skin.getTailImage() ;
					} else {
						img = skin.getBodyImage() ;
					}

					double ax = segment.getX() - dx + x;
					double ay = segment.getY() - dy + y;
					// Test d'occlusion, si le segment du serpent est en dehors de la fenêtre on ne l'affiche pas pour
					// économiser les ressources de l'ordinateur.
					if (ax + bodyWidth >= 0 && ay + bodyWidth >= 0 && ax <= settings.getWindowWidth() && ay <= settings.getWindowHeight()) {
						trans.setToTranslation(segment.getX() - dx + x, segment.getY() - dy + y) ;
						trans.scale(bodyWidth / img.getWidth(), bodyWidth / img.getHeight()) ;
						trans.rotate(segment.getAngle() + Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2) ;
						g.drawImage(img, trans, null) ;

						if (segment instanceof WeakSegment) {
							g.setColor(Color.WHITE) ;
							g.fillOval((int) segment.getX() - dx + x - 1, (int) segment.getY() - dy + y - 1, (int) bodyWidth + 2, (int) bodyWidth + 2) ;
						} else if (segment instanceof ShieldSegment) {
							g.setColor(new Color(128, 128, 128, 200)) ;
							g.fillOval((int) segment.getX() - dx + x - 1, (int) segment.getY() - dy + y - 1, (int) bodyWidth + 2, (int) bodyWidth + 2) ;
						}
					}
				}

				// Nom d'utilisateur
				Segment head = body.get(0) ;
				g.setColor(Color.WHITE) ;
				g.drawString(username, (int) head.getX() - dx + x, (int) head.getY() - dy + y - 15) ;
			}
		}
	}

	public List<Segment> getBody() {
		return body;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public Skin getSkin() {
		return skin;
	}
	
	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	public double getBodyWidth() {
		return bodyWidth;
	}

	public void setBodyWidth(double bodyWidth) {
		this.bodyWidth = bodyWidth;
	}

	public double getSpeed() {
		return speed;
	}

	public double getSpeedBoost() {
		return speedBoost;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void addSnakeListener(SnakeListener listener) {
		snakeListeners.add(listener);
	}

	public void removeSnakeListener(SnakeListener listener) {
		snakeListeners.remove(listener);
	}

	public void setSnakeListeners(ArrayList<SnakeListener> snakeListeners) {
		this.snakeListeners = snakeListeners;
	}

	public void setLastPositions(LinkedList<Point2D.Double> lastPositions) {
		this.lastPositions = lastPositions;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
