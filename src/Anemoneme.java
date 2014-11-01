import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Eine steuerbare Anemoneme.
 * 
 * @author Tom Nordloh
 *
 */
public class Anemoneme {

	private Image anemoneme;
	private Image anemonemeOriginal;
	private Image controls;
	private final int SPEED = 5;
	private final double SCALE = 0.2;
	private int ROTATION_VALUE = 5;
	private int x,y;
	private int points;
	
	private boolean spawn = false;
	private int MAX_SPAWN_FRAMES = 300;
	private int spawn_frame = 1;
	
	private List<Bean> beans;
	
	public enum directions{
		UP, DOWN, LEFT, RIGHT
	}
	
	/**
	 * Konstruktor generiert eine Anemoneme.
	 * Punktestand wird auf 0 gesetzt.
	 * 
	 * @param beans Die anderen Spielobjekte
	 * @param x X-Position
	 * @param y Y-Position
	 * @throws SlickException 
	 */
	public Anemoneme(List<Bean> beans, int x, int y){
		try {
			anemonemeOriginal = (new Image("res/anemoneme.png")).getScaledCopy((float) SCALE);
			anemoneme = anemonemeOriginal;
			controls = (new Image("res/controls.png"));
		} catch (SlickException e) {}
		this.x = x;
		this.y = y;
		this.beans = beans;
		this.points = 0;
		spawn = true;
	}
		
	public boolean intersects(Bean bean){
		return false;
	}
	
	public int getPoints(){
		return points;
	}

	public void draw() {
				
		if(spawn) {
			
			if(spawn_frame >= MAX_SPAWN_FRAMES) {
				
				try {controls.destroy();} catch (SlickException e) {}
				anemoneme = anemonemeOriginal;
				spawn = false;
				
			} else {
			
				controls.draw(10,10);
				anemoneme = anemonemeOriginal.getScaledCopy( (float) (
						(double) spawn_frame / (double) MAX_SPAWN_FRAMES
						));
				Angle.set(Angle.get() + (int) (20 * Math.sin((Math.PI*spawn_frame) / MAX_SPAWN_FRAMES)));
				spawn_frame++;
				
			}
			
		} else {
			
			Bean intersector = getOneIntersectingBean();
			if(intersector != null) {
				points += intersector.getPoints();
				intersector.getEaten();
			}
		}
		
		anemoneme.setRotation((float) (Angle.get() - 90)); // -90 und die 'Ausrichtung' der Bilddatei auszugleichen
		anemoneme.drawCentered(x, y);
	}

	public void move(directions direction, int delta) {
		
		if(spawn)
			return;
		
		delta /= 10;
		
		switch(direction){
		
		case UP:
			y -= SPEED * delta;
			
			if(Angle.get() != 90) {
				
				if(Angle.get() > 90 && Angle.get() <= 270) {
					Angle.set(Angle.get() - (Angle.get() - 90)/ROTATION_VALUE);
				} else {
					if (Angle.get() < 90) {
						Angle.set(Angle.get() + (90 - Angle.get())/ROTATION_VALUE);
					} else {
						Angle.set(Angle.get() + (450 - Angle.get())/ROTATION_VALUE);
					}
				}
			}
			break;
		
		case DOWN:
			y += SPEED * delta;
			
			if(Angle.get() != 270) {
				
				if(Angle.get() >= 90 && Angle.get() < 270) {
					Angle.set(Angle.get() + (270 - Angle.get())/ROTATION_VALUE);
				} else {
					if (Angle.get() > 270) {
						Angle.set(Angle.get() - (Angle.get() - 270)/ROTATION_VALUE);
					} else if (Angle.get() >= 0) {
						Angle.set(Angle.get() - (90 + Angle.get())/ROTATION_VALUE);
					}
				}
			}
			break;
			
		case LEFT:
			x -= SPEED * delta;
			
			if(Angle.get() != 0) {
				
				if(Angle.get() >= 180) {
					Angle.set(Angle.get() + (360 - Angle.get())/ROTATION_VALUE);
				} else {
					Angle.set(Angle.get() - Angle.get()/ROTATION_VALUE);
				}
			}
			break;
		
		case RIGHT:
			x += SPEED * delta;
			
			if(Angle.get() != 180) {
				
				if(Angle.get() >= 0 && Angle.get() < 180) {
					Angle.set(Angle.get() + (180 - Angle.get())/ROTATION_VALUE);
				} else {
					Angle.set(Angle.get() - (Angle.get() - 180)/ROTATION_VALUE);
				}
			}
			break;
		}
	}
	
	private Bean getOneIntersectingBean() {
		
		for (Bean bean : beans) {
			if(bean.getX() >= x - bean.getImage().getWidth()/2
					&& bean.getX() <= x + bean.getImage().getWidth()/2
					&& bean.getY() >= y - bean.getImage().getHeight()/2
					&& bean.getY() <= y + bean.getImage().getHeight()/2)
				return bean;
		}
		
		return null;
	}

	public int getX() {return x;}
	public int getY() {return y;}
	
	/**
	 * Statische Klasse die den Winkel speichert.
	 * Sichergestellter Wertebereich: ]0:359[
	 * Bsp.: Angle.set(-10) setzt den Wert 350
	 */
	private static class Angle{
		
		static int value = 0;
		
		public static void set(int i) {
			
			if(i < 0) {
				set(360 + i);
			} else if(i > 359) {
				set(i - 360);
			} else {
				value = i;
			}
		}
		
		public static int get() {
			return value;
		}
	}
}
