import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends BasicGame{

	//Abmessung des Spielfensters und Framerate
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FRAMERATE = 60;
	
	//Schrift für den Punktestand
	private static Font SCORE_FONT;
	private static TrueTypeFont SCORE_TRUE_TYPE_FONT;
	
	//Spielobjekte
	public static int BEANS_COUNT = 10;
	private List<Bean> beans = new ArrayList<Bean>();
	Anemoneme anemoneme;
	
	//MouseMomento zum speichern der vorherigen Mausposition
	MouseMomento m = MouseMomento.getInstance();
	
	public MainFrame(){
		super("Manuelas JellyBean Game");
	}
	
	/**
	 * Initialisierungsmethode wird nur einmal beim Start ausgeführt.
	 * Hier ist der richtige Ort um ein Spielfeld, Spieler (...) zu erzeugen.
	 */
	public void init(GameContainer gc) throws SlickException{
				
		for (int i=0; i<BEANS_COUNT; i++)
			beans.add(new Bean(gc,WIDTH,HEIGHT));
		
		SCORE_FONT = new Font("Verdana", Font.PLAIN, 18);
		SCORE_TRUE_TYPE_FONT = new TrueTypeFont(SCORE_FONT, false);
	}
	
	/**
	 * Updatemethode wird immer wieder in einem bestimmten Intervall ausgeführt.
	 * Hier ist der richtige Ort für Tastatureingaben und Berechnungen der
	 * Animationspfade.
	 */
	public void update(GameContainer gc, int delta) throws SlickException{
		// Eingabegeräte abfragen
		Input input = gc.getInput();
		double mouseX = input.getMouseX();
		double mouseY = input.getMouseY();
		double deltaX = mouseX - m.getX();
		double deltaY = mouseY - m.getY();
		m.setX(mouseX);
		m.setY(mouseY);
		
		// Spiel beenden
		if(input.isKeyDown(Input.KEY_ESCAPE))
			System.exit(0);
		
		// Bean bei klick grabben
		if(input.isMouseButtonDown(0)) {
			for (Bean b : beans)
				if (mouseX > b.getX()-b.getImage().getWidth()/2
						&& mouseX < b.getX()+b.getImage().getWidth()/2
						&& mouseY > b.getY()-b.getImage().getHeight()/2
						&& mouseY < b.getY()+b.getImage().getHeight()/2) {
					b.setMovespeed(Math.min(6, 
							(int) Math.sqrt(deltaX*deltaX + deltaY*deltaY)));
					int newDirection = (int) Math.toDegrees(Math.acos( deltaX / Math.sqrt(deltaX*deltaX + deltaY*deltaY) ));
					if (deltaY>0) newDirection *= -1;
					b.setDirection(newDirection);
					b.setX((int) mouseX);
					b.setY((int) mouseY);
					
					// Bean an den Anfang des Arrays stellen damit diese nicht beim ziehen ausgetauscht wird
					if (beans.get(0) != b) {
						beans.set(beans.indexOf(b), beans.get(0));
						beans.set(0, b);
					}
					
					break; 
				}
		}
		
		// Bean animieren
		for (int i=0; i<BEANS_COUNT; i++)
			beans.get(i).move(delta);
		
		// Spawn Anemoneme
		if(input.isKeyDown(Input.KEY_LCONTROL)
				&& input.isKeyDown(Input.KEY_ENTER)
				&& anemoneme == null)
				anemoneme = new Anemoneme(beans, WIDTH/2, HEIGHT/2);
		
		// Steuerung Anemoneme
		if(anemoneme != null){
			if((input.isKeyDown(Input.KEY_W)
					|| input.isKeyDown(Input.KEY_UP))
					&& anemoneme.getY() > 0){
				anemoneme.move(Anemoneme.directions.UP, delta);
			}
			if((input.isKeyDown(Input.KEY_A)
					|| input.isKeyDown(Input.KEY_LEFT))
					&& anemoneme.getX() > 0){
				anemoneme.move(Anemoneme.directions.LEFT, delta);
			}
			if((input.isKeyDown(Input.KEY_S)
					|| input.isKeyDown(Input.KEY_DOWN))
					&& anemoneme.getY() < HEIGHT){
				anemoneme.move(Anemoneme.directions.DOWN, delta);
			}
			if((input.isKeyDown(Input.KEY_D)
					|| input.isKeyDown(Input.KEY_RIGHT))
					&& anemoneme.getX() < WIDTH){
				anemoneme.move(Anemoneme.directions.RIGHT, delta);
			}
		}
	}
	
	/**
	 * Grafisches Rendern der Spielobjekte, wird so oft ausgeführt wie es geht.
	 * Hier ist der richtige Ort für Draw-Methoden von Linien, Polygonen, 
	 * Kreisen, Bildern und so weiter.
	 */
	public void render(GameContainer gc, Graphics g) throws SlickException{
		
		//Bean zeichnen
		for (int i=0; i<BEANS_COUNT; i++)
			beans.get(i).draw();
				
		//falls Anemoneme im Spiel ist zeichne sie und ihren Punktestand
		if(anemoneme != null){
			anemoneme.draw();
		
		//Punktestand zeichnen
		String scoreText = "Jellybeans gegessen: " + anemoneme.getPoints();
		SCORE_TRUE_TYPE_FONT.drawString(
				WIDTH/2 - SCORE_TRUE_TYPE_FONT.getWidth(scoreText)/2,
				SCORE_TRUE_TYPE_FONT.getHeight(scoreText)/2, 
				scoreText);
		}
	}
	
	public static void main(String[] args) throws SlickException{
		
		AppGameContainer game = new AppGameContainer(new MainFrame());
		game.setDisplayMode(WIDTH, HEIGHT, false);
		game.setVSync(true);
		game.setTargetFrameRate(FRAMERATE);
		game.setShowFPS(true);
		game.setFullscreen(false);
		game.start();
	}
	
}
