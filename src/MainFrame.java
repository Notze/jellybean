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
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();
		
		// Spiel beenden
		if(input.isKeyDown(Input.KEY_ESCAPE))
			System.exit(0);
		
		// Bean bei klick anhalten
		if(input.isMousePressed(0)) {
			// TODO
			// Unterm Cursors nach bean suchen
			// bean anhalten
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
		String scoreText = "Punktestand: " + anemoneme.getPoints();
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
