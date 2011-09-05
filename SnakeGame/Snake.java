import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Snake 
{
	private final int PWIDTH, PHEIGHT;
	private static final int squareSize = 10;
	
	//Direction connstants
	public static final int up = 0;
	public static final int right = 1;
	public static final int down = 2;
	public static final int left = 3;
	
	private int currentDirection = up; //Current direction, initial the snake will be going up
	
	private SnakeGame sg;
	private PointPickUp pointBox;
	private SnakePanel sp;
	
	private Area snakeBody;
	private ArrayList<Rectangle> snakeBoxes;
	
	//Changeable properties by a SpecialPickUp
	private Color snakeColor = Color.RED;
	private boolean hasCollisionToSelf = true;
	private boolean shouldAddBoxes = true;
	private boolean collisionToWall = true;
	
	private int boxesToBeAdded = 0;
	
	private ArrayDeque<Integer> moveQueue = new ArrayDeque<Integer>();
	
	public Snake(int PWIDTH, int PHEIGHT, SnakeGame sg, SnakePanel sp) 
	{
		this.PWIDTH = PWIDTH;
		this.PHEIGHT = PHEIGHT;
		
		this.sg = sg;
		this.sp = sp;
		
		snakeBoxes = new ArrayList<Rectangle>();
		snakeBody = new Area();
		snakeBoxes.add(new Rectangle((PWIDTH / 2) - 10, (PHEIGHT - 10) , squareSize, squareSize)); //Initial snake is 1 box long and start it in bottom middle of panel
		snakeBody.add(new Area(snakeBoxes.get(0)));
		
	}
	
	//Logic behind the move is that for the snakeArea, only the front and the end of the snake needs to be updated.
	//While for the ArrayList each Rectangle at nth position needs to be updated with the n - 1 rectangle's bounds. Up until the head
	//Then move head (ArrayList[0]) based on current direction  
	public synchronized void move()
	{
		if(moveQueue != null && moveQueue.size() > 0)
			currentDirection = moveQueue.poll();
		
		if(boxesToBeAdded == 0)
		{
				snakeBody.subtract(new Area(snakeBoxes.get(snakeBoxes.size() - 1))); // subtract the tail from the snake
		}
		else
		{
			snakeBoxes.add(new Rectangle());
			boxesToBeAdded--;
		}
		
		for(int i = (snakeBoxes.size() - 1); i > 0; i--)
		{
			snakeBoxes.get(i).setBounds(snakeBoxes.get(i - 1));
		}
		switch(currentDirection)
		{
			case up: 
				snakeBoxes.get(0).translate(0, -10);
				break;
				
			case right:
				snakeBoxes.get(0).translate(10, 0);
				break;
					
			case down:
				snakeBoxes.get(0).translate(0, 10);
				break;
					
			case left:
				snakeBoxes.get(0).translate(-10, 0);
				break;
		}
		if(hasCollisionToSelf)	
			isCollidingWithSelf();
		hasHitWall();
		snakeBody.add(new Area(snakeBoxes.get(0)));
		hasHitPickUp();
	}	
	
	public void isCollidingWithSelf()
	{
		if(snakeBody.intersects((Rectangle2D)snakeBoxes.get(0)))
		{
				sp.setGameOver();
		}
	}
	
	public void hasHitPickUp()
	{
		if(snakeBody.intersects(pointBox.getCurrentRectangle()))
		{
			pointBox.doPickUpAbility();
			pointBox.generateLocation();
		}
		
		//Checks if there's an actual need to test for SpecialPickUp
		if(sp.getManager().shouldRender()) 
			if(snakeBody.intersects(sp.getManager().getSpawnedPickUp().getCurrentRectangle()))
			{
				sp.getManager().hasBeenCaptured();
			}
	}
	
	public void hasHitWall()
	{
		if((snakeBoxes.get(0).getX() < 0 || snakeBoxes.get(0).getX() >= sp.getWidth()) ||  
			(snakeBoxes.get(0).getY() < 0 || snakeBoxes.get(0).getY() >= sp.getHeight()))
		{
			if(collisionToWall)
			{
				sp.setGameOver();	
			}
			else
			{
				if(snakeBoxes.get(0).getX() < 0)
				{
					snakeBoxes.get(0).setLocation(PWIDTH - 10, (int)snakeBoxes.get(0).getY());
					System.out.println("too left: " + snakeBoxes.get(0).getLocation());
				}
				else
				{
					if(snakeBoxes.get(0).getX() >= PWIDTH)
					{
						snakeBoxes.get(0).setLocation(0, (int)snakeBoxes.get(0).getY());
						System.out.println("too right: " + snakeBoxes.get(0).getLocation());
					}
					else
					{
						if(snakeBoxes.get(0).getY() >= PHEIGHT)
						{
							snakeBoxes.get(0).setLocation((int)snakeBoxes.get(0).getX(), 0);
							System.out.println("too down: " + snakeBoxes.get(0).getLocation());
						}
						else
						{
							snakeBoxes.get(0).setLocation((int)snakeBoxes.get(0).getX(), PHEIGHT - 10);
							System.out.println("too up: " + snakeBoxes.get(0).getLocation());
						}
					}
					
				}		
			}
		}
	}
	
	public void addPointPickUp(PointPickUp box)
	{
		this.pointBox = box;
	}
	
	public synchronized void changeDirection(int newDirection)
	{
		if((newDirection != currentDirection - 2) && (newDirection != currentDirection + 2))
		{
			moveQueue.add(newDirection);
			currentDirection = newDirection;
		}
	}
	
	public void draw(Graphics2D g)
	{
		
		g.setColor(snakeColor);
		if(hasCollisionToSelf && collisionToWall)
		{
			g.fill(snakeBody);
		}
		else
		{
			g.draw(snakeBody);
		}
	}
	
	public void setCollisionToSelf(boolean bUseCollision)
	{
		hasCollisionToSelf = bUseCollision;
	}
	
	public void setShouldAddBoxes(boolean addBoxes)
	{
		shouldAddBoxes = addBoxes;
	}
	
	public void setCollisionToWall(boolean collides)
	{
		collisionToWall = collides;
	}
	
	public void addBoxes(int numBoxes)
	{
		if(shouldAddBoxes)	
			boxesToBeAdded += numBoxes;
	}
	
	public void addPoints(int score)
	{
		sg.updateScore(score);
	}
	
	public Area getSnakeArea()
	{
		return snakeBody;
	}
	
	public void setColor(Color color)
	{
		snakeColor = color;
	}	
}
