import java.awt.Color;
import java.util.TimerTask;
import javax.swing.JTextField;

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
		sp.getSnake().setShouldAddBoxes(false);
		sp.getSnake().setColor(Color.MAGENTA);
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
						sp.getSnake().setShouldAddBoxes(true);
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
