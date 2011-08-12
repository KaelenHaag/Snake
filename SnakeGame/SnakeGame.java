import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JFrame implements WindowListener
{
	private static int DEFAULT_FPS = 80;
	
	private SnakePanel sp;
	private JTextField scoreJTextField; //Current score of the game
	private JLabel pickUpEffectDurationJLabel;
	private JTextField pickUpEffectDurationJTextField;
	
	private int currentScore = 0;
	
	public SnakeGame(long period) 
	{
		super("Snake");
		makeGUI(period);
		
		addWindowListener(this);
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	private void makeGUI(long period)
	{
		Container contentPane = getContentPane();
		
		sp = new SnakePanel(this, period);
		contentPane.add(sp, "Center");
		
		JPanel infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.X_AXIS));
		
		scoreJTextField = new JTextField("Current score: 0");
		scoreJTextField.setEditable(false);
		infoArea.add(scoreJTextField);
		
		infoArea.add(Box.createHorizontalGlue());
		
		pickUpEffectDurationJLabel= new JLabel("Time Left for Powerup:");
		infoArea.add(pickUpEffectDurationJLabel);
		
		pickUpEffectDurationJTextField = new JTextField("0");
		pickUpEffectDurationJTextField.setEditable(false);
		infoArea.add(pickUpEffectDurationJTextField);
		
		contentPane.add(infoArea, "South");
	}
	
	public JTextField getTimeJTextField()
	{
		return pickUpEffectDurationJTextField;
	}
	
	
	public void updateScore(int points)
	{
		currentScore += points;
		scoreJTextField.setText("Current score: " + currentScore);
	}
	
	public int getScore()
	{
		return currentScore;
	} 	
		
	//Implemented WindowListener methods	
	public void windowActivated(WindowEvent e) 
	{sp.resumeGame();}
	
	public void windowDeactivated(WindowEvent e) 
	{sp.pauseGame();}

	public void windowDeiconified(WindowEvent e) 
	{sp.pauseGame();}

	public void windowIconified(WindowEvent e) 
	{sp.resumeGame();}

	public void windowClosing(WindowEvent e)
  	{  sp.stopGame();  }

  	public void windowClosed(WindowEvent e) {}
  	public void windowOpened(WindowEvent e) {}
  	
  	
  	public static void main(String args[])
  	{
  		int fps = DEFAULT_FPS;
  		if(args.length != 0)
  			fps = Integer.parseInt(args[0]);
  			
  		long period = (long)1000.0/fps;
  		System.out.println("fps: " + fps + "; period: " + period + " ms");
  		
  		new SnakeGame(period*1000000L);
  	}			
}
