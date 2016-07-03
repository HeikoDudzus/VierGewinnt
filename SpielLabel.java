import java.awt.*;
/**
 * @author Lucia Scheibe
 * @version 
 */
public class SpielLabel extends javax.swing.JLabel

{
    java.awt.Color gameColor;

    public SpielLabel()
    {
        super("");
        gameColor=java.awt.Color.GRAY;

    }
    
    public void setGameColor(java.awt.Color pColor)
    {
        gameColor=pColor;
        repaint();
    }

    public void paint(java.awt.Graphics g){
        super.paint(g);
       
        //if (gameColor ==java.awt.Color.BLACK) return; 
         g.setColor(java.awt.Color.BLACK);
         g.fillOval(0, 0,this.getWidth(),this.getHeight());
        
         g.setColor(gameColor);
         g.fillOval(2, 2,this.getWidth()-4,this.getHeight()-4);
        
        
       
    }
}