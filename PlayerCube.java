import java.awt.Color;
import java.awt.Rectangle;


public class PlayerCube 
{
	int xPos = 0;
	int yPos = 0;
	int playerDem = 20;
	Color c = Color.orange;
	
	public Rectangle getBounds() 
	{
        return new Rectangle(xPos, yPos, playerDem, playerDem);
    }
	
	public void setY(int y)
	{
		yPos = y;
	}
	public void setX(int x)
	{
		xPos = x;
	}

	public int getY() 
	{
		return yPos;
	}

	public int getX() 
	{
		return xPos;
	}
	
	public int getDem()
	{
		return playerDem;
	}
	
	public Color getColor()
	{
		return c;
	}

	public void setColor(Color c) 
	{
		this.c = c;
	}

}
