package csci_442_image_processing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MyPanel extends JPanel
{
 
int startX, flag, startY, endX, endY;

    BufferedImage grid;
    Graphics2D gc;

	public MyPanel()
	{
	   startX = startY =0;
	   endX=100;
	   endY=100;
	   flag = 0;
       addMouseListener(new MouseComp());
 	}

 	public void clear()
 	{
 	    repaint();
 	  }
    public void paintComponent(Graphics g)
    {  
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         if(grid == null){
            int w = this.getWidth();
            int h = this.getHeight();
            grid = (BufferedImage)(this.createImage(w,h));
            gc = grid.createGraphics();

         }
         g2.drawImage(grid, null, 0, 0);
     }
    public void drawing()
    {
        
        gc.drawLine(startX, startY, endX, endY);
        repaint();
    }
    
	public class MouseComp implements MouseListener
	{
	   public void mouseClicked(MouseEvent e) {}
       public void mouseEntered(MouseEvent e) {}    
       public void mousePressed(MouseEvent e) 
       {
          startX = e.getX();
	      startY = e.getY();
	      drawing();
	   }
	   
       public void mouseReleased(MouseEvent e){} 
       public void mouseExited(MouseEvent evt){}
	}

}
