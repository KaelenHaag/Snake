import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Random;

public class RandomPickUp extends SpecialPickUp
{
	private Random colorRandomizer = new Random();

	public RandomPickUp(SnakePanel sp, SpecialPickUpManager manager) 
	{
		super(sp, manager);
	}
	
	public void doPickUpAbility()
	{
		manager.chooseType();
		manager.getSpawnedPickUp().doPickUpAbility();
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
