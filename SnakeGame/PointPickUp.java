import java.awt.*;
import java.util.Random;
import java.awt.geom.*;

public class PointPickUp extends PickUp 
{
	private final int POINTSTOADD = 10;
	private final int BOXESTOADD = 3;
	
	private Color drawColor = Color.PINK;
	
	public PointPickUp(SnakePanel sp)
	{
		super(sp);
	}
	
	public synchronized void generateLocation()
	{
		//Do normal spawning
		super.generateLocation();
		
		//But now check to see if there is a SpecialPickUp on the map, if true check for no intersection between the two
		if(getSnakePanel().getManager().shouldRender())
			if(new Area(getCurrentRectangle()).intersects(getSnakePanel().getManager().getSpawnedPickUp().getCurrentRectangle()))
			{
				System.out.println("COLLISION! POINT");
				generateLocation();
			}
	}
	
	public void doPickUpAbility()
	{
		getSnakePanel().getSnake().addPoints(POINTSTOADD);
		getSnakePanel().getSnake().addBoxes(BOXESTOADD);
	}
}