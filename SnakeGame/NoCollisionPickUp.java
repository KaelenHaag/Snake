import java.awt.*;
import java.util.*;
import javax.swing.*;

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
		getSnakePanel().getSnake().setCollisionToSelf(false);
		getSnakePanel().getSnake().setColor(Color.GREEN);
		getTimer().schedule(new TimerTask()
		{
			int tick = 0;
			public void run()
			{
				if(!getSnakePanel().isPaused())
				{
					tick++;
					timeJTextField.setText(Integer.toString(LIFETIME - tick));
					if(tick >= LIFETIME)
					{
						getSnakePanel().getSnake().setCollisionToSelf(true);
						getSnakePanel().getSnake().setColor(Color.RED);
						getManager().destroyCurrentSpecialPickUp();
						getTimer().cancel();
						getTimer().purge();
				
					}
				}
			}
		}, 1000, 1000);
	}	
}
