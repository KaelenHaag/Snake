import java.awt.*;
import java.util.*;
import javax.swing.*;

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
		getSnakePanel().getSnake().setCollisionToWall(false);
		getSnakePanel().getSnake().setColor(Color.CYAN);
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
						getSnakePanel().getSnake().setCollisionToWall(true);
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
