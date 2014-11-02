import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.opengl.Texture;


public class Bean {

	private Graphics g;
	private Image image;
	private List<Image> images = new ArrayList<Image>();
	private final int MAX_MOVE_SPEED = 4;
	private final int MIN_MOVE_SPEED = 1;
	private final int MAX_ROTATION_SPEED = 1;
	private final double MIN_SCALE_SIZE = 0.2; 
	private int moveSpeed;
	private double rotationSpeed;
	private double direction;
	private double angle = 0;
	private double scale;
	private int x,y;
	private int WIDTH, HEIGHT;
	private int INIT_POINTS = 10;
	private int points = INIT_POINTS;
	
	private boolean isGettingEaten = false;
	private int MAX_GET_EATEN_FRAMES = 100;
	private int getEatenFrame = 1;
	
	public Bean(GameContainer gc, int width, int height) throws SlickException{
		this.g = gc.getGraphics();
		this.WIDTH = width;
		this.HEIGHT = height;
		
		for (int i=1; i<=30; i++)
			images.add(new Image("res/bean"+i+".png"));
		
		spawn();
	}
	
	/**
	 * Gibt den Alphawert an der angegebenen Koordinate des bildes zurück.
	 * 
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @return das byte des Alphawertes
	 */
	public byte getAlphaAt(int x, int y) {
		
		Texture texture = image.getTexture();
		byte[] textureData = texture.getTextureData();
		
		return textureData[4 * (y * texture.getTextureWidth() + x) + 3];
	}
	
	/**
	 * Animiert dieses Objekt. Das beinhaltet die Bewegung über den Bildschirm 
	 * und die Rotation.
	 */
	public void move(int delta){
		
		delta /= 10;
		
		if(g != null){

			// Bewegung
			double deltaY = -Math.sin(Math.toRadians(direction)) * moveSpeed;
			double deltaX = Math.cos(Math.toRadians(direction)) * moveSpeed;
			
			x += Math.round(deltaX * delta);
			y += Math.round(deltaY * delta);
			
			if (beanIsOutOfSight())
				spawn();
			
			// Rotation
			image.setRotation((float) angle);
			angle += rotationSpeed;
		}
	}
	
	public void draw() {
		
		if(isGettingEaten) {
			
			if(getEatenFrame >= MAX_GET_EATEN_FRAMES) {
				isGettingEaten = false;
				getEatenFrame = 1;
				points = INIT_POINTS;
				spawn();
			}
			
			image = image.getScaledCopy((float) 0.8);
			
			getEatenFrame++;
			
		}
		
		image.drawCentered(x, y);
	}
	
	/**
	 * Lässt die JellyBean außerhalb des Bildschirms erscheinen und sich
	 * durch das Bild bewegen.
	 */
	public void spawn(){
		
		// Zufälliges Bild in zufälliger Größe
		scale = (double) (Math.random() * (1 - MIN_SCALE_SIZE) + MIN_SCALE_SIZE);
		int imageNr = (int) (Math.random() * (images.size() - 0.001));
		image = images.get(imageNr).getScaledCopy((float) scale);
		
		direction = (int) (Math.random() * 360);
		moveSpeed = (int) (Math.random() * (MAX_MOVE_SPEED - MIN_MOVE_SPEED) + MIN_MOVE_SPEED);
		rotationSpeed = Math.pow(-1, (int) (Math.random() * 2)) * Math.random() * MAX_ROTATION_SPEED;
		
		// Zufallsposition (Faktor) am Bildschirmrand innerhalb der mittleren 80%
		double randFactor = Math.random() * 0.8 + 0.1;
		
		if(direction < 45 || direction >= 315 ){		 //Blickrichtung rechts
			x = -image.getWidth()/2;
			y = (int) (HEIGHT * randFactor);
		} else if(direction >= 45 && direction < 135 ){	 //Blickrichtung oben
			x = (int) (WIDTH * randFactor);
			y = HEIGHT + image.getHeight()/2;
		} else if(direction >= 135 && direction < 225 ){ //Blickrichtung links
			x = WIDTH + image.getWidth()/2;
			y = (int) (HEIGHT * randFactor);
		} else {										 //Blickrichtung unten
			x = (int) (WIDTH * randFactor);
			y = -image.getHeight()/2;
		}
	}
	
	/**
	 * Wird von Anemoneme aufgerufen wenn die Bean gefressen wird.
	 */
	public void getEaten() {
		points = 0;
		moveSpeed = 0;
		rotationSpeed = 0;
		isGettingEaten = true;
	}
	
	/**
	 * Gibt true zurück wenn dieses Object 10px außerhalb des sichtbaren 
	 * Bereichs liegt.
	 */
	private boolean beanIsOutOfSight() {	
		if (x <= -image.getHeight()/2 - 10 
				|| x >= WIDTH+ image.getHeight()/2 +10 
				|| y <= -image.getWidth()/2 - 10 
				|| y >= HEIGHT+ image.getWidth()/2 +10)
			return true;
		return false;
	}
	
	// Getter und Setter
	public Image getImage() {return image;}
	public int getX() {return x;}
	public int getY() {return y;}
	public int getPoints() {return points;}
	public void setMovespeed(int speed) {moveSpeed = speed;}
	public void setDirection(int direction) {this.direction = direction;}
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	
}
