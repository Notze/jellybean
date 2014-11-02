
public class MouseMomento {

	private double x = 0;
	private double y = 0;
	private static MouseMomento mouseMomento;
	
	private MouseMomento(){}
	
	public static MouseMomento getInstance() {
		if (mouseMomento == null)
			mouseMomento = new MouseMomento();
		return mouseMomento;
	}
	
	public void setX(double x) {this.x = x;}
	public void setY(double y) {this.y = y;}
	public double getX() {return x;}
	public double getY() {return y;}
}
