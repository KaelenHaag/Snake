import java.awt.Color;
import java.util.TimerTask;
import javax.swing.JTextField;

//This pickup disables the snakes collision so it can't collide with itself for the specified time period.
public class NoCollisionPickUp extends SpecialPickUp
{
	private final int LIFETIME = 15;
	
	public NoCollisionPickUp(SnakePanel sp, SpecialPickUpManager manager) 
	{
		super(sp, manager);
		setColor(Color.GREEN);
	}
	
	public void doPickUpAbility()
	{
		sp.getSnake().setCollisionToSelf(false);
		sp.getSnake().setColor(Color.GREEN);
		lifeTimer.schedule(new TimerTask()
		{
			int tick = 0;
			public void run()
			{
				if(!sp.isPaused())
				{
					tick++;
					timeJTextField.setText(Integer.toString(LIFETIME - tick));
					if(tick >= LIFETIME)
					{
						sp.getSnake().setCollisionToSelf(true);
						sp.getSnake().setColor(Color.RED);
						manager.destroyCurrentSpecialPickUp();
						lifeTimer.cancel();
						lifeTimer.purge();
				
					}
				}
			}
		}, 1000, 1000);
	}	
}
