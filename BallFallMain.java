import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

	/*****************************************************
	 * Primary class for the game inherits from Applet
	 *****************************************************/
	@SuppressWarnings("serial")
	public class BallFallMain  extends Applet implements Runnable, KeyListener//, MouseMotionListener 
	{
	    //the main thread becomes the game loop
	    Thread gameloop;

	    //use this as a double buffer
	    BufferedImage backbuffer;

	    //the main drawing object for the back buffer
	    Graphics2D g2d;

	    //toggle for drawing bounding boxes
//	    boolean showBounds = false;

	    //create the identity transform (0,0)
//	    AffineTransform identity = new AffineTransform();
	    
	    int Gamewidth = 640;
	    int Gameheight = 480;
	    Rectangle border = new Rectangle(0,0,Gamewidth,Gameheight);
	    
	    PlayerCube player = new PlayerCube(); 
	    
	    long startTime = System.nanoTime();
		ArrayList<Floors> floors = new ArrayList<Floors>();
		int sleepCounter = 15;
		boolean touchingFloor = false;
		boolean touchingSafeZone = false;
		int score = 0;
		int playerSpeed = 5;
		boolean pause = false;
		boolean rightHeld = false;
		boolean leftHeld= false;
		long estimatedTime;
		boolean youLose = false;
		boolean disableLeft = false;
		boolean disableRight = false;
		
		
		public void init() 
	    {
	    	//create the back buffer for smooth graphics
	        backbuffer = new BufferedImage(Gamewidth, Gameheight, BufferedImage.TYPE_INT_RGB);
	        g2d = backbuffer.createGraphics();
	        
	        setSize(650, 500);
	        setFocusable(true);

	        //start the user input listener
	        addKeyListener(this);
	        //addMouseMotionListener(this);
	        
	        startTime = System.nanoTime();
	    }
	    
	    /*****************************************************
		 * thread start event - start the game loop running
		 *****************************************************/
		public void start() 
		{
			//create the gameloop thread for real-time updates
		    gameloop = new Thread(this);
		    gameloop.start();
		}

		/*****************************************************
		 * thread run event (game loop)
		 *****************************************************/
		public void run() 
		{
		    //acquire the current thread
		    Thread t = Thread.currentThread();
		
		    //keep going as long as the thread is alive
		    while (t == gameloop) 
		    {
		    	if (!pause)
		    	{
		    		try 
		            {
		    			
		    			startRunTime = System.nanoTime();
		    			//update the game loop
		                gameUpdate();
		                runTime = startRunTime - System.nanoTime();
		                Thread.sleep(sleepCounter - TimeUnit.NANOSECONDS.toMillis(runTime));
		            }
		            catch(InterruptedException e) {
		                e.printStackTrace();
		            }
		    		catch(IllegalArgumentException e)
		    		{
		    			e.printStackTrace();
		    		}
		            repaint();
		    	} 
		    }
		}
		
		long startRunTime, runTime;

		/*****************************************************
		 * thread stop event
		 *****************************************************/
		public void stop() 
		{
			//kill the gameloop thread
		    gameloop = null;
		}

		/*****************************************************
		 * applet window repaint event--draw the back buffer
		 *****************************************************/
		public void paint(Graphics g) 
		{
			//draw the back buffer onto the applet window
		    g.drawImage(backbuffer, 0, 0, this);
		}

		public void update(Graphics g) 
	    {
	    	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    	
	        //start off transforms at identity
//	        g2d.setTransform(identity);

	        //erase the background
	        g2d.setPaint(Color.BLACK);
	        g2d.fillRect(0, 0, getSize().width, getSize().height);

	        drawFloors();
	        drawPlayer();
	        
	        g2d.setColor(Color.white);
	        g2d.drawString("Spawn Speed: " + floorSpawnSpeed/10 + " Seconds Per Floor", 420, 16);
	        g2d.drawString("Score: " + score, 580, 36);
	        
	        g2d.drawString("Cameron O'Neil", 540, Gameheight-16);
	        
	        if (youLose)
	        {
	        	g2d.drawString("You Lose", 300, 260);
	        	g2d.drawString("Final Score: " + score, 300, 280);
	        }
	        
	        //repaint the applet window
	        paint(g);
	    }
	    
	    
	    private void drawFloors() 
	    {
	    	for (int i=0; i<floors.size(); i++)
	        {
	        	if (!retro && !flashing)
	        	{
	    			g2d.setPaint(floors.get(i).getFloorColor());
	    			g2d.fillRect(floors.get(i).getBounds().x, floors.get(i).getBounds().y, floors.get(i).getBounds().width, floors.get(i).getBounds().height);
	        	}
	        	else if (retro)
	        	{
	        		g2d.setPaint(Color.green);
	        		g2d.drawRect(floors.get(i).getBounds().x, floors.get(i).getBounds().y, floors.get(i).getBounds().width, floors.get(i).getBounds().height);
	        		g2d.drawLine(floors.get(i).getGap().x -1, floors.get(i).getGap().y, floors.get(i).getGap().x -1, floors.get(i).getGap().y + floors.get(i).getGap().height-1);
	        	}
	        	else if (flashing)
				{
					g2d.setColor(getRandomColor());
					g2d.fillRect(floors.get(i).getBounds().x, floors.get(i).getBounds().y, floors.get(i).getBounds().width, floors.get(i).getBounds().height);
				}
	        	
		        g2d.setPaint(Color.BLACK);
		        g2d.fillRect(floors.get(i).getGap().x, floors.get(i).getGap().y, floors.get(i).getGap().width, floors.get(i).getGap().height);
	        }
	    }


		private void drawPlayer() 
	    {
			if (!retro && !flashing && !playerFlashing)
        	{
				g2d.setColor(player.getColor());
		    	g2d.fillRect(player.getX(), player.getY(), player.getDem(), player.getDem());
        	}
			else if (retro && !playerFlashing)
			{
				g2d.setColor(Color.green);
		    	g2d.drawRect(player.getX(), player.getY(), player.getDem(), player.getDem());
			}
			else if (flashing || playerFlashing)
			{
				g2d.setColor(getRandomColor());
				g2d.fillRect(player.getX(), player.getY(), player.getDem(), player.getDem());
			}
	    }
		

		private void gameUpdate() 
	    {
	    	if (!youLose)
	    	{
	    		movePlayerAndFloors();
		    	controls();
		    	spawnFloors();
		    	updateFloors();
		    	moveGap();
		    	checkIfPlayerInBounds();
		    	updateSpeed();
	    	}
	    }
		
		private void moveGap() 
		{
			for (int i=0; i<floors.size(); i++)
		    {
				if (floors.get(i).getDirection().equals("right"))
				{
					floors.get(i).setGapX(floors.get(i).getGapX()+1);
				}
				else
				{
					floors.get(i).setGapX(floors.get(i).getGapX()-1);
				}
				
				if (floors.get(i).getGapX() <= 0)
				{
					floors.get(i).setDirection("right");
				}
				else if (floors.get(i).getGapX() + floors.get(i).getSafeGap().width > Gamewidth)
				{
					floors.get(i).setDirection("left");
				}
		    }
		}

		double floorSpawnSpeed = 15;
		int difficulty = 1;

	    private void updateSpeed() 
	    {
	    	if (difficulty > 16)
//	    	if (difficulty > 5)
	    	{
	    		difficulty = 1;
	    		
	    		if (floorSpawnSpeed > 6)
	    		{
	    			floorSpawnSpeed -= .5;
	    		}
	    	}
		}
	    
		private void movePlayerAndFloors() 
		{
			touchingSafeZone = false;
			for (int i=0; i<floors.size(); i++)
		    {
				if (floors.get(i).getSafeGap().contains(player.getBounds()))
				{
					touchingSafeZone = true;
					
					if (!floors.get(i).hasPointBeenGiven())
					{
						floors.get(i).gavePoint(); //Give point if have not already
						score++;
						difficulty++;
					}
				}
		    }
			if (touchingSafeZone)
			{
				Gravity();
			}
			else
			{
				touchingFloor = false;
		    	for (int i=0; i<floors.size(); i++)
		        {
		    		if (floors.get(i).getSafeBounds().contains(player.getBounds()))
		    		{
		    			touchingFloor = true;
		    		}
		        }
		    	
		    	if (touchingFloor)
		    	{
		    		antiGravity();
		    	}
		    	else 
		    	{
		    		Gravity();
		    	}
			}
		}

		private void controls() 
	    {
	    	boolean intersectingRight = false;
		    boolean intersectingLeft = false;

	    	//Right
	    	if (rightHeld && !disableRight)
	    	{
	    		for (int i=0; i<floors.size(); i++)
		        {
	    			if (floors.get(i).getGap().intersects(player.getBounds()))
		    		{
	    				intersectingRight = true;
	    				if (!((player.getBounds().x + player.getDem()) > (floors.get(i).getGap().x + floors.get(i).getGap().width)))
	    				{
	    					player.setX(player.getX() + playerSpeed);
	    				}
		    		}
		        }
	    		if (!intersectingRight)
	    		{
	    			player.setX(player.getX() + playerSpeed);
	    		}
	    	}
	    	
	    	//Left
	    	if (leftHeld && !disableLeft)
	    	{
	    		for (int i=0; i<floors.size(); i++)
		        {
	    			if (floors.get(i).getGap().intersects(player.getBounds()))
		    		{
	    				intersectingLeft = true;
	    				if (!((player.getBounds().x) < (floors.get(i).getGap().x)))
	    				{
	    					player.setX(player.getX() - playerSpeed);
	    				}
		    		}
		        }
	    		if (!intersectingLeft)
	    		{
	    			player.setX(player.getX() - playerSpeed);
	    		}
	    	}
		}
		
		private void spawnFloors() 
		{
			estimatedTime = (System.nanoTime() - startTime)*10;
			
			if (TimeUnit.NANOSECONDS.toSeconds(estimatedTime) > floorSpawnSpeed)
			{
				floors.add(new Floors(Gameheight+10, floorSpawnSpeed));
				startTime = System.nanoTime();
			}
		}

		private void updateFloors() 
		{
			for (int i=0; i<floors.size(); i++)
		    {
				floors.get(i).setyPos(floors.get(i).getBounds().y -1);
				
				if (floors.get(i).getY() < -60)
				{
					//Remove Floors when they go off Screen
					floors.remove(i);
				}
		    }
		}

		private void checkIfPlayerInBounds() 
		{
			if (player.getBounds().y < -20)
			{
				youLose = true;
			}
			else if (player.getBounds().y + player.getDem() > Gameheight)
			{
				//If player touching bottom, this will cause them to stay there.
				antiGravity();
			}
			if (player.getBounds().x < 1) //Don't let player go past Left border
			{
				disableLeft = true;
//				player.setX(player.getX() + playerSpeed);
			}
			else
			{
				disableLeft = false;
//				player.setX(player.getX() - playerSpeed);
			}
			
			if (player.getBounds().x + player.getDem() > Gamewidth -1) //Don't let player go past right border
			{
				disableRight = true;
//				player.setX(player.getX() - playerSpeed);
			}
			else
			{
				disableRight = false;
			}
		}
		
		private void Gravity() 
		{
			player.setY(player.getY() +1);
		}

		private void antiGravity() 
		{
			player.setY(player.getY() -1);
		}


		/*****************************************************
	     * key listener events
	     *****************************************************/
	    public void keyReleased(KeyEvent k) 
	    {
	    	 int keyCode = k.getKeyCode();

	         switch (keyCode) 
	         {
		         case KeyEvent.VK_LEFT:
		         case KeyEvent.VK_A:
		        	 leftHeld = false;
		             break;
		
		         case KeyEvent.VK_RIGHT:
		         case KeyEvent.VK_D: 
		        	 rightHeld = false;
		             break;
	         }
	    }
	    public void keyTyped(KeyEvent k) {}
	    public void keyPressed(KeyEvent k) 
	    {
	        int keyCode = k.getKeyCode();

	        switch (keyCode) 
	        {
	        	case KeyEvent.VK_LEFT:
	        	case KeyEvent.VK_A:
	        		leftHeld = true;
		            break;
		
	        	case KeyEvent.VK_RIGHT:
		        case KeyEvent.VK_D:
		        	rightHeld = true;
		            break;
		        case KeyEvent.VK_PLUS:
		        	sleepCounter++;
		            break;
		        case KeyEvent.VK_MINUS:
		        	sleepCounter--;
		            break;
		         
		        //Toggles:
		        case KeyEvent.VK_NUMPAD0:
		        	player.setColor(Color.blue);
		            break;
		        case KeyEvent.VK_NUMPAD1:
		        	player.setColor(Color.red);
		            break;
		        case KeyEvent.VK_NUMPAD2:
		        	player.setColor(Color.cyan);
		            break;
		        case KeyEvent.VK_NUMPAD3:
		        	player.setColor(Color.green);
		            break;
		        case KeyEvent.VK_NUMPAD4:
		        	player.setColor(Color.LIGHT_GRAY);
		            break;
		        case KeyEvent.VK_NUMPAD5:
		        	player.setColor(Color.magenta);
		            break;
		        case KeyEvent.VK_NUMPAD6:
		        	player.setColor(Color.orange);
		            break;
		        case KeyEvent.VK_NUMPAD7:
		        	player.setColor(Color.pink);
		            break;
		        case KeyEvent.VK_NUMPAD8:
		        	player.setColor(Color.white);
		            break;
		        case KeyEvent.VK_NUMPAD9:
		        	player.setColor(Color.yellow);
		            break;
		        case KeyEvent.VK_ENTER:
		        	retro = !retro;
		            break;
		        case KeyEvent.VK_P:
		        	pause = !pause;
		        	if (pause)
		        	{
		        		pauseStart = System.nanoTime();
		        	}
		        	else
		        	{	        		
		        		startTime += (/* pausedTime = */ System.nanoTime() - pauseStart);
		        	}
		        	break;
		        case KeyEvent.VK_F:
		        	flashing = !flashing;
		        	break;
		        case KeyEvent.VK_R:
		        	playerFlashing = !playerFlashing;
		        	break;
		        	
	        }
	    }
	    
	    long pauseStart;
	    boolean flashing = false;
	    boolean retro = false;
	    boolean playerFlashing = false;
	    
	    private Color getRandomColor() 
		{
			Color colorOptions[] = {Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED};
		    int rand = (int) (Math.random()*8);
		    return colorOptions[rand];
		}
	    
	    public static void main(String[] args) 
		{
			BallFallMain bob = new BallFallMain();
			bob.setVisible(true);
		}

//		@Override
//		public void mouseDragged(MouseEvent arg0) {}
//		
//		int mouseX = 0, mouseY = 0;
//
//		@Override
//		public void mouseMoved(MouseEvent m) 
//		{
//			mouseX = m.getX();
//			mouseY = m.getY();
//		}
		
//		private void say(String Str)
//		{
//			System.out.println(Str);
//		}
//		private void say(int i)
//		{
//			System.out.println(i);
//		}
	


}
