import java.awt.Color;
import java.awt.Rectangle;

public class Floors 
{
	int width = 640;
	int height = 22;
	int xPosition = 0;
	int yPosition = 200;//480;
	int gapWidth = 40;
	
	public Floors(int y, double floorSpawnSpeed)
	{
		yPosition = y;
		
		//15,14,13,12,11,10,9,8,7,6
		
		if (floorSpawnSpeed >= 15)
		{
			sub=0;
		}
		else if (floorSpawnSpeed >= 14)
		{
			sub=1;
		}
		else if (floorSpawnSpeed >= 13)
		{
			sub=2;
		}
		else if (floorSpawnSpeed >= 12)
		{
			sub=3;
		}
		else if (floorSpawnSpeed >= 11)
		{
			sub=4;
		}
		else if (floorSpawnSpeed >= 10)
		{
			sub=5;
		}
		else if (floorSpawnSpeed >= 9)
		{
			sub=6;
		}
		else if (floorSpawnSpeed >= 8)
		{
			sub=7;
		}
		else if (floorSpawnSpeed >= 7)
		{
			sub=8;
		}
		else if (floorSpawnSpeed >= 6)
		{
			sub=9;
		}
		
		if (rand > .5)
		{
			direction = "right";
		}
		else
		{
			direction = "left";
		}
	}
	
	int sub = 0;
	
	//Color colorOptions[] = {Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED};
	Color colorOptions[] = {Color.LIGHT_GRAY, Color.CYAN, Color.BLUE, Color.GREEN,
							Color.YELLOW, Color.ORANGE, Color.PINK, Color.MAGENTA, new Color(159,0,255), Color.RED};
    //int rand = (int) (Math.random()*8);
    //public Color getFloorColor() {return colorOptions[rand];}
	public Color getFloorColor() {return colorOptions[sub];}
	
	double rand = Math.random();
	String direction;
	
	public String getDirection()
	{
		return direction;
	}

	public void setyPos(int y)
	{
		yPosition = y;
	}
	
	public int getY()
	{
		return yPosition;
	}
	
	public void setGapX(int x)
	{
		gap1 = x;
	}
	
	public int getGapX()
	{
		return gap1;
	}
	
	
	public Rectangle getBounds()
	{
		return new Rectangle(xPosition,yPosition,width, height);
	}
	
	public Rectangle getSafeBounds()
	{
		return new Rectangle(xPosition,yPosition-20,width, height);
	}
	
	
	//Min + (int)(Math.random() * ((Max - Min) + 1))
	int min = 40;
	int max = width-40;
	int gap1 = min + (int)(Math.random() * ((max - min) + 1));
	
	public Rectangle getGap()
	{
		return new Rectangle(gap1,yPosition,gapWidth, height+1);
	}
	
	public Rectangle getSafeGap()
	{
		return new Rectangle(gap1,yPosition-20,gapWidth, height);
	}
	
	
	boolean givenPoint = false;
	
	public void gavePoint()
	{
		givenPoint = true;
	}
	
	public boolean hasPointBeenGiven()
	{
		return givenPoint;
	}

	public void setDirection(String dir) 
	{
		direction = dir;
	}

}
