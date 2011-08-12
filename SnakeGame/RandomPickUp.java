import java.awt.*;
import java.util.*;

public class RandomPickUp extends SpecialPickUp
{
	private Random colorRandomizer = new Random();

	public RandomPickUp(SnakePanel sp, SpecialPickUpManager manager) 
	{
		super(sp, manager);
	}
	
	public void doPickUpAbility()
	{
		getManager().chooseType();
		getManager().getSpawnedPickUp().doPickUpAbility();
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(randomizeColor());
		g.fill(currentRectangle);	
	}
	
	public Color randomizeColor()
	{
		Color randomColor = new Color(colorRandomizer.nextInt(256), colorRandomizer.nextInt(256), colorRandomizer.nextInt(256));
		return randomColor;
	}
}
