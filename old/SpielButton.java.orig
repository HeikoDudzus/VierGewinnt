
/**
 * @author 
 * @version 
 */
public class SpielButton extends javax.swing.JButton

{
    java.awt.Color gameColor;

    public SpielButton()
    {
        super("");
        gameColor=java.awt.Color.BLACK;

    }
    
    public void setGameColor(java.awt.Color pColor)
    {
        gameColor=pColor;
    }

    public void paint(java.awt.Graphics g){
        super.paint(g);
        if (gameColor ==java.awt.Color.BLACK) return; 
         g.setColor(gameColor);
         g.fillOval(0, 0,this.getWidth(),this.getHeight());
       
    }
}