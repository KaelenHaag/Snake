//Kaelen Haag
import java.awt.*;
import java.util.*;
import javax.swing.*;

//This pickup disables the snake from gaining boxes after picking up a PointPickUp. 
public class AddNoBoxesPickUp extends SpecialPickUp 
{
	private final int LIFETIME = 15;
	
	public AddNoBoxesPickUp(SnakePanel sp, SpecialPickUpManager manager) 
	{
		super(sp, manager);
		setColor(Color.MAGENTA);
	}
	
	public void doPickUpAbility()
	{
		getSnakePanel().getSnake().setShouldAddBoxes(false);
		getSnakePanel().getSnake().setColor(Color.MAGENTA);
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
						getSnakePanel().getSnake().setShouldAddBoxes(true);
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
