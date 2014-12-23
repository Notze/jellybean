import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends BasicGame{

	//Abmessung des Spielfensters und Sonstiges
	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static int FRAMERATE = 60;
	private static boolean VSYNC = true;
	private static boolean SHOWFPS = false;
	private static boolean FULLSCREEN = false;
	private static boolean SOUND = true;
	
	//Schrift für den Punktestand
	private static Font SCORE_FONT;
	private static TrueTypeFont SCORE_TRUE_TYPE_FONT;
	
	//Spielobjekte
	public static int BEANS_COUNT = 10;
	private List<Bean> beans = new ArrayList<Bean>();
	Anemoneme anemoneme;
	Music moveLoop;
	
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
		moveLoop = new Music("res/rocket.wav");
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
			Boolean anyAction = false;
			if((input.isKeyDown(Input.KEY_W)
					|| input.isKeyDown(Input.KEY_UP))
					&& anemoneme.getY() > 0){
				anemoneme.move(Anemoneme.directions.UP, delta);
				anyAction = true;
			}
			if((input.isKeyDown(Input.KEY_A)
					|| input.isKeyDown(Input.KEY_LEFT))
					&& anemoneme.getX() > 0){
				anemoneme.move(Anemoneme.directions.LEFT, delta);
				anyAction = true;
			}
			if((input.isKeyDown(Input.KEY_S)
					|| input.isKeyDown(Input.KEY_DOWN))
					&& anemoneme.getY() < HEIGHT){
				anemoneme.move(Anemoneme.directions.DOWN, delta);
				anyAction = true;
			}
			if((input.isKeyDown(Input.KEY_D)
					|| input.isKeyDown(Input.KEY_RIGHT))
					&& anemoneme.getX() < WIDTH){
				anemoneme.move(Anemoneme.directions.RIGHT, delta);
				anyAction = true;
			}
			
			if (SOUND) {
				if(!anyAction) {
					moveLoop.stop();
				} else if(!moveLoop.playing()) {
					moveLoop.play();
				}
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
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new FileReader("./settings.cfg"));
			String line = null;
			while ((line = in.readLine()) != null) {
				if(line.length()>=5) {
					String option = line.substring(0, 5);
					switch(option){
					case "width":
						WIDTH = Integer.parseInt(line.substring(6));
						break;
					case "heigh":
						HEIGHT = Integer.parseInt(line.substring(7));
						break;
					case "frame":
						FRAMERATE = Integer.parseInt(line.substring(10));
						break;
					case "showF":
						SHOWFPS = Boolean.valueOf(line.substring(8));
						break;
					case "fulls":
						FULLSCREEN = Boolean.valueOf(line.substring(11));
						break;
					case "vSync":
						VSYNC = Boolean.valueOf(line.substring(6));
						break;
					case "beans":
						BEANS_COUNT = Integer.parseInt(line.substring(6));
						break;
					case "moveS":
						SOUND = Boolean.valueOf(line.substring(10));
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		game.setDisplayMode(WIDTH, HEIGHT, false);
		game.setVSync(VSYNC);
		game.setTargetFrameRate(FRAMERATE);
		game.setShowFPS(SHOWFPS);
		game.setFullscreen(FULLSCREEN);
		game.start();
	}
	
}
