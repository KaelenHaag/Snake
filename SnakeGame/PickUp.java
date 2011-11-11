//Kaelen Haag
import java.awt.*;
import java.util.Random;
import java.awt.geom.*;


public class PickUp 
{
	private Color drawColor = Color.BLUE;  
	
	Rectangle currentRectangle;
	
	private int boxesToAdd = 3;
	private int pointValue = 10;
	
	private SnakePanel sp;
	
	private Random generator = new Random();
	
	public PickUp(SnakePanel sp) 
	{
		this.sp = sp;
		generateLocation();
	}
	
	//Generate location for the pickup inside the panel's bounds.
	//Check if intersecting with the Snake, if true generate a new location
	public synchronized void generateLocation()
	{
		int x = generator.nextInt(sp.getWidth()/10) * 10;
		int y = generator.nextInt(sp.getHeight()/ 10) *10;
		currentRectangle = new Rectangle(x,y,10,10);
		if(sp.getSnake().getSnakeArea().intersects(currentRectangle))
		{
			System.out.println("COLLISION!");
			generateLocation();
		}
			
		System.out.println(currentRectangle);
	}
	
	public Rectangle getCurrentRectangle()
	{
		return currentRectangle;
	}
	
	public int getBoxesToAdd()
	{
		return boxesToAdd;
	}
	
	public int getPointValue()
	{
		return pointValue;
	}
	
	public void setColor(Color newColor)
	{
		drawColor = newColor;
	}
	
	public void doPickUpAbility(){}
	
	public SnakePanel getSnakePanel()
	{
		return sp;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(drawColor);
		g.fill(currentRectangle);
	}	
}
