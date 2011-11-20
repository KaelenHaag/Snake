import java.util.Timer;
import java.awt.geom.*;
import javax.swing.JTextField;


public class SpecialPickUp extends PickUp 
{
	private final int LIFETIME = 15;
	
	protected Timer lifeTimer = new Timer(true);
	protected SpecialPickUpManager manager;
	//Reference to pickUpEffectDurationJTextField in SnakeGame. So the pickup can notify the player of the time left for the active pickup.
	protected final JTextField timeJTextField;
	
	public SpecialPickUp(SnakePanel sp, SpecialPickUpManager manager)
	{
		super(sp);
		this.manager = manager;
		timeJTextField = sp.getSnakeGame().getTimeJTextField();		
	}
	
	public synchronized void generateLocation()
	{
		//Go through normal generation process
		super.generateLocation();
		
		//Go through new check to see if this pickup collides with a pointpickup
		if(new Area(getCurrentRectangle()).intersects(getSnakePanel().getPointPickUp().getCurrentRectangle()))
		{
			System.out.println("COLLISION! SPECIAL");
			generateLocation();
		}
	}
	
	public SpecialPickUpManager getManager()
	{
		return manager;
	}
	
	public Timer getTimer()
	{
		return lifeTimer;
	}
}
