import java.util.*;

public class SpecialPickUpManager 
{
	private Timer specialPickUpSpawnTimer; //= new Timer("specialPickUp", true);
	private int spawnTime = 0;
	private Random randomGenerator = new Random();
	
	private boolean beenCaptured = false; //These can be facts
	private boolean shouldRender = false;
	
	private SpecialPickUp special;
	
	private SnakePanel sp;
	
	public SpecialPickUpManager(SnakePanel sp) 
	{
		this.sp = sp;
	}
	
	public void startSpawnTimer()
	{
		calcSpawnTime();
		specialPickUpSpawnTimer = new Timer("specialPickUp", true); //Need to create a new Timer because old one was cancelled(DESTROYED) and the manager isnt recreated throughout the game's lifetime
		specialPickUpSpawnTimer.schedule(new TimerTask()
		{
			int tick = 0;
			public void run()
			{
				if(!sp.isPaused())
				{
					tick++;
					System.out.println(tick);
					if(tick > spawnTime)
					{
						spawnSpecialPickUp();
						specialPickUpSpawnTimer.cancel();
						specialPickUpSpawnTimer.purge();
					}
				}
			}
		}, 1000, 1000);
	}
	
	public void calcSpawnTime()
	{
		//generate number between 30(inclusive) - 56(exclusive)
		spawnTime = (randomGenerator.nextInt(26) + 30); //Spawn time in s
		System.out.println(spawnTime);
	}
	
	public void spawnSpecialPickUp()
	{
		chooseType();
		special.generateLocation();
		shouldRender = true;
		beenCaptured = false;
	}
	
	//destroys the pickup and starts the spawning process
	public void destroyCurrentSpecialPickUp()
	{
		special = null;
		shouldRender = false;
		beenCaptured = false;
		startSpawnTimer();		
	}
	
	//Randomizes the type of the SpecialPickUp spawned
	//To add a new type  do randomGenerator.nextInt(n+1) and add a new case for the new value
	public void chooseType()
	{
		int choice = randomGenerator.nextInt(4);
		switch(choice)
		{
			case 0:
				special = new NoCollisionPickUp(sp, this);
				break;
			case 1:
				special = new AddNoBoxesPickUp(sp, this);
				break;
			case 2:
				special = new NoCollisionToWallPickUp(sp, this);
				break;
			case 3:
				special = new RandomPickUp(sp, this);
				break;
		}
	}
	
	//Called by the Snake to begin the SpecialPickUp's ability and Timer
	public void hasBeenCaptured()
	{
		beenCaptured = true;
		shouldRender = false;
		special.doPickUpAbility();
	}
		
	public Timer getSpawnTimer()
	{
		return specialPickUpSpawnTimer;
	}
	
	public SpecialPickUp getSpawnedPickUp()
	{
		return special;
	}
	
	//Used mainly to see if the pickup is active on the gameboard and should be rendered. 
	//Used by rendering system and other game objects (Snake, point, etc. See if they need to account for the SpecialPickUp) 
	public boolean shouldRender()
	{
		return shouldRender;
	}
	
	public boolean beenCaptured()
	{
		return beenCaptured;
	}	
}
