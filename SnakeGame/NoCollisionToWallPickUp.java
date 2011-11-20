import java.awt.Color;
import java.util.TimerTask;
import javax.swing.JTextField;

//This pickup disables the snake from running into the wall. Places head on the opposite of the side that was entered.
public class NoCollisionToWallPickUp extends SpecialPickUp 
{
	private final int LIFETIME = 25;
	
	public NoCollisionToWallPickUp(SnakePanel sp, SpecialPickUpManager manager) 
	{
		super(sp, manager);
		setColor(Color.CYAN);
	}
	
	public void doPickUpAbility()
	{
		sp.getSnake().setCollisionToWall(false);
		sp.getSnake().setColor(Color.CYAN);
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
						sp.getSnake().setCollisionToWall(true);
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
