import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

//Some code (some rendering, stat reporting, and overly commented stuff) used from the book "Killer Game Programming in Java" by Andrew Davison.
//The rest (game logic, some rendering, others) is done by me and me only. 
public class SnakePanel extends JPanel implements Runnable
{
	
	private static final int PWIDTH = 500;
	private static final int PHEIGHT = 400;
	
	private static long MAX_STATS_INTERVAL = 1000000000L;
  // private static long MAX_STATS_INTERVAL = 1000L;
    // record stats every 1 second (roughly)

  	private static final int NO_DELAYS_PER_YIELD = 16;
  /* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */

  	private static int MAX_FRAME_SKIPS = 5;   // was 2;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

 	private static int NUM_FPS = 10;
     // number of FPS values stored to get an average


  	// used for gathering statistics
  	private long statsInterval = 0L;    // in ns
  	private long prevStatsTime;   
  	private long totalElapsedTime = 0L;
  	private long gameStartTime;
  	private int timeSpentInGame = 0;    // in seconds

  	private long frameCount = 0;
  	private double fpsStore[];
  	private long statsCount = 0;
  	private double averageFPS = 0.0;

  	private long framesSkipped = 0L;
  	private long totalFramesSkipped = 0L;
  	private double upsStore[];
  	private double averageUPS = 0.0;


  	private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
  	private DecimalFormat timedf = new DecimalFormat("0.####");  // 4 dp
	
	//References to the game, snake, and current pickup box.
	private SnakeGame sg;
	private Snake bob;
	private PointPickUp pointBox;
	
	private SpecialPickUpManager manager;
	
	private Thread animator;
	private boolean running = false;
	private boolean isPaused = false;
	
	private long period;
	
	private boolean gameOver = false;
	private Font font;
	private FontMetrics metrics;
	
	private Graphics2D dbg;
	private Image dbImage = null;
	
	
	public SnakePanel(SnakeGame sg, long period) 
	{
		this.sg = sg;
		this.period = period;
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		
		setFocusable(true);
		requestFocus();
		readyForTermination();
		
		manager = new SpecialPickUpManager(this);
		
		bob = new Snake(PWIDTH, PHEIGHT, sg, this);
		pointBox = new PointPickUp(this);
		bob.addPointPickUp(pointBox);
		
		setupKeyBindings();
		
		font = new Font("sanserif", Font.BOLD, 24);
		metrics = this.getFontMetrics(font);
		
		// initialise timing elements
    	fpsStore = new double[NUM_FPS];
    	upsStore = new double[NUM_FPS];
    	for (int i=0; i < NUM_FPS; i++) 
    	{
      		fpsStore[i] = 0.0;
      		upsStore[i] = 0.0;
    	}
	}
	
	private void readyForTermination()
  	{
		addKeyListener( new KeyAdapter() {
		// listen for esc, q, end, ctrl-c on the canvas to
		// allow a convenient exit from the full screen configuration
      	public void keyPressed(KeyEvent e)
       	{ int keyCode = e.getKeyCode();
        	 if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             	(keyCode == KeyEvent.VK_END) ||
             	((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
           	running = false;
         }
       }
     });
  	}  // end of readyForTermination()
  	
  	
  	//KeyBindings
  	private void setupKeyBindings()
  	{
  		Action moveUp = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				bob.changeDirection(Snake.up);
  			}
  		};
  		
  		Action moveDown = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				bob.changeDirection(Snake.down);
  			}
  		};
  		
  		Action moveLeft = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				bob.changeDirection(Snake.left);
  			}
  		};
  		
  		Action moveRight = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				bob.changeDirection(Snake.right);
  			}
  		};
  		
  		Action newGame = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				if(gameOver)
  					startNewGame();
  			}
  		};
  		
  		Action testMove = new AbstractAction()
  		{
  			public void actionPerformed(ActionEvent e)
  			{
  				bob.changeDirection(Snake.right);
  				bob.changeDirection(Snake.up);
  				bob.changeDirection(Snake.left);
  				bob.changeDirection(Snake.down);
  			}
  		};
  		
  		getInputMap().put(KeyStroke.getKeyStroke("UP"),"up");
  		getInputMap().put(KeyStroke.getKeyStroke("DOWN"),"down");
  		getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),"right");
  		getInputMap().put(KeyStroke.getKeyStroke("LEFT"),"left");
  		getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "space");
  		
  		getActionMap().put("up", moveUp);
  		getActionMap().put("down", moveDown);
  		getActionMap().put("right", moveRight);
  		getActionMap().put("left", moveLeft);
  		getActionMap().put("space", newGame);	 		
  	}
  	
  	public void addNotify()
  	{
  		super.addNotify();
  		startGame();
  	}
  	
  	private void startGame()
  	{
  		if(animator == null || !running)
  		{
  			animator = new Thread(this);
  			animator.start();
  			new Timer(64, new AbstractAction() // gets rid of movement being relient on FPS, easily adjustable if needed.
  			{
  				public void actionPerformed(ActionEvent e)
  				{
  					if(!isPaused && !gameOver)
  						bob.move();
  				}
  			}
  			).start();
  			
  			manager.startSpawnTimer();
  		}
  	}
  	
  	// ------------- game life cycle methods ------------
  	// called by the JFrame's window listener methods


  	public void resumeGame()
 	// called when the JFrame is activated / deiconified
  	{  
  		isPaused = false;
  	} 


  	public void pauseGame()
  	// called when the JFrame is deactivated / iconified
  	{ 
  		isPaused = true;
  	} 


  	public void stopGame() 
  	// called when the JFrame is closing
  	{  running = false;   }

  	// ----------------------------------------------
	
	public void setGameOver()
	{
		gameOver = true;
		
		//If there isn't a spawned pickup that means the spawn timer is running so that needs to be cancelled(DESTROYED!)
		if(manager.getSpawnedPickUp() == null && !manager.beenCaptured())
		{
			manager.getSpawnTimer().cancel();
			manager.getSpawnTimer().purge();
		}
		//The pickup has been captured so that Timer must be cancelled(DESTOYED!) If there is a special pickup on the map it'll be destoyed in the restart process
		else
		{
			manager.getSpawnedPickUp().getTimer().cancel();
			manager.getSpawnedPickUp().getTimer().purge();
		}
	}
	
	public void startNewGame()
	{
		bob = new Snake(PWIDTH, PHEIGHT, sg, this);
		pointBox = new PointPickUp(this);
		bob.addPointPickUp(pointBox);
		
		if(manager.getSpawnedPickUp() == null)
		{
			manager.startSpawnTimer();
		}
		else
		{
			manager.destroyCurrentSpecialPickUp();
			sg.getTimeJTextField().setText("0");	
		}
		
		sg.updateScore(-sg.getScore());
		gameOver = false;
	}
	
	public void run()
	{
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		
		gameStartTime = System.nanoTime();
		prevStatsTime = gameStartTime;
		beforeTime = gameStartTime;
		
		running = true; 
		
		while(running)
		{
			gameUpdate();
			gameRender();
			paintScreen();
			
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
			
			if(sleepTime > 0)
			{
				try
				{
					Thread.sleep(sleepTime/1000000L);
				}
				catch(InterruptedException e){}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else
			{
				excess -= sleepTime;
				overSleepTime = 0L;
				
				if(++noDelays >= NO_DELAYS_PER_YIELD)
				{
					Thread.yield();
					noDelays = 0;
				}				
			}
			
			beforeTime = System.nanoTime();
			
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS))
			{
				excess -= period;
				gameUpdate();
				skips++;
			}
			framesSkipped += skips;
			
			storeStats();
			
		}
		printStats();
		System.exit(0);
	}
	
	//Dont really need this but dont really feel like messing with it
	private void gameUpdate()
	{
		if(!isPaused && !gameOver)
		{
			//bob.move();
		}
	}
	
	private void gameRender()
	{
		if(dbImage == null)
		{
			dbImage = createImage(PWIDTH, PHEIGHT);
			if(dbImage == null)
			{
				System.out.println("dbImage is null");
				return; 
			}
			else
				dbg = (Graphics2D)dbImage.getGraphics();
		}
		
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0,0,PWIDTH,PHEIGHT);
		
		dbg.setColor(Color.GREEN);
		dbg.setFont(font);
		
		dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS), 20, 25);
		
		dbg.setColor(Color.BLACK);
		
		bob.draw(dbg);
		pointBox.draw(dbg);
		if(manager.shouldRender())
		{
			manager.getSpawnedPickUp().draw(dbg);
		}
		
		
		if(gameOver)
		{
			gameOverMessage(dbg);
		}
	}
	
	private void gameOverMessage(Graphics g)
	{
		String msg = "Game Over! Your Score: " + sg.getScore(); 
		String msg2 = "Press the spacebar to play again.";
		int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
		int y = (PHEIGHT - metrics.getHeight()) / 2;
		g.setColor(Color.ORANGE);
		g.setFont(font);
		g.drawString(msg, x, y);
		x = (PWIDTH - metrics.stringWidth(msg2)) / 2;
		y += metrics.getHeight();
		g.drawString(msg2, x, y);
	}
	
	private void paintScreen()
	{
		Graphics g;
		try
		{
			g = this.getGraphics();
			if((g != null) && (dbImage != null))
			{
				g.drawImage(dbImage, 0, 0, null);
			}
			g.dispose();
		}
		catch(Exception e)
		{
			System.out.println("Graphics context error: " + e);
		}
	}
	
	public boolean isPaused()
	{
		return isPaused;
	}
	
	public Snake getSnake()
	{
		return bob;
	}
	
	public SpecialPickUpManager getManager()
	{
		return manager;
	}
	
	public PointPickUp getPointPickUp()
	{
		return pointBox;
	}
	
	public SnakeGame getSnakeGame()
	{
		return sg;
	}
	
	public int getWidth()
	{
		return PWIDTH;
	}
	
	public int getHeight()
	{
		return PHEIGHT;
	}
	
	
  private void storeStats()
  /* The statistics:
       - the summed periods for all the iterations in this interval
         (period is the amount of time a single frame iteration should take), 
         the actual elapsed time in this interval, 
         the error between these two numbers;

       - the total frame count, which is the total number of calls to run();

       - the frames skipped in this interval, the total number of frames
         skipped. A frame skip is a game update without a corresponding render;

       - the FPS (frames/sec) and UPS (updates/sec) for this interval, 
         the average FPS & UPS over the last NUM_FPSs intervals.

     The data is collected every MAX_STATS_INTERVAL  (1 sec).
  */
  { 
    frameCount++;
    statsInterval += period;

    if (statsInterval >= MAX_STATS_INTERVAL) {     // record stats every MAX_STATS_INTERVAL
      long timeNow = System.nanoTime();
      timeSpentInGame = (int) ((timeNow - gameStartTime)/1000000000L);  // ns --> secs
      

      long realElapsedTime = timeNow - prevStatsTime;   // time since last stats collection
      totalElapsedTime += realElapsedTime;

      double timingError = 
         ((double)(realElapsedTime - statsInterval) / statsInterval) * 100.0;

      totalFramesSkipped += framesSkipped;

      double actualFPS = 0;     // calculate the latest FPS and UPS
      double actualUPS = 0;
      if (totalElapsedTime > 0) {
        actualFPS = (((double)frameCount / totalElapsedTime) * 1000000000L);
        actualUPS = (((double)(frameCount + totalFramesSkipped) / totalElapsedTime) 
                                                             * 1000000000L);
      }

      // store the latest FPS and UPS
      fpsStore[ (int)statsCount%NUM_FPS ] = actualFPS;
      upsStore[ (int)statsCount%NUM_FPS ] = actualUPS;
      statsCount = statsCount+1;

      double totalFPS = 0.0;     // total the stored FPSs and UPSs
      double totalUPS = 0.0;
      for (int i=0; i < NUM_FPS; i++) {
        totalFPS += fpsStore[i];
        totalUPS += upsStore[i];
      }

      if (statsCount < NUM_FPS) { // obtain the average FPS and UPS
        averageFPS = totalFPS/statsCount;
        averageUPS = totalUPS/statsCount;
      }
      else {
        averageFPS = totalFPS/NUM_FPS;
        averageUPS = totalUPS/NUM_FPS;
      }
/*
      System.out.println(timedf.format( (double) statsInterval/1000000000L) + " " + 
                    timedf.format((double) realElapsedTime/1000000000L) + "s " + 
			        df.format(timingError) + "% " + 
                    frameCount + "c " +
                    framesSkipped + "/" + totalFramesSkipped + " skip; " +
                    df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " + 
                    df.format(actualUPS) + " " + df.format(averageUPS) + " aups" );
*/
      framesSkipped = 0;
      prevStatsTime = timeNow;
      statsInterval = 0L;   // reset
    }
  }  // end of storeStats()


  private void printStats()
  {
    System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
	System.out.println("Average FPS: " + df.format(averageFPS));
	System.out.println("Average UPS: " + df.format(averageUPS));
    System.out.println("Time Spent: " + timeSpentInGame + " secs");
    System.out.println("Current Score: " + sg.getScore());
  }  // end of printStats()


		
}
