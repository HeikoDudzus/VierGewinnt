/**
 * @author Gerrit (Die Hinterbänkler)
 * @version 2016-06-12
 */

public class Spielfeld
{
    private Feld spielfeldflaeche[][];

    public Spielfeld()
    {
        spielfeldflaeche = new Feld[7][7];
        for(int i=0; i<7; i++)
        {
            for(int j=0; j<7; j++)
            {
                spielfeldflaeche[i][j] = new Feld();
            }
        }
    }

    public void setzeXaufFeld(int pX, int pY)
    {
        spielfeldflaeche[pX][pY].setzeX();
    }

    public void setzeOaufFeld(int pX, int pY)
    {
        spielfeldflaeche[pX][pY].setzeO();
    }

    public String gibZustandDesFeldes(int pX, int pY)
    {
        return spielfeldflaeche[pX][pY].gibZustand();
    }

    public boolean gibIstSpielfeldVoll()
    {
        boolean alleFelderBeschriftet = true;
        for(int i=0; i<7; i++)
        {
            for(int j=0; j<7; j++)
            {
                if(spielfeldflaeche[i][j].gibZustand().equals("leer"))
                {
                    alleFelderBeschriftet = false;
                }
            }
        }
        return alleFelderBeschriftet;
    }

    public void setzeNeuesSpielfeld()
    {
        for(int i=0; i<7; i++)
        {
            for(int j=0; j<7; j++)
            {
                spielfeldflaeche[i][j].setzeLeer();
            }
        }
    }
    
    public String toString() {
        String [] zeichen = {" ", "X", "O"};
        int groesse = 7;
        String out = "";
        out += "+";
        for (int x=0; x < groesse; x++) {
            out += "-";
        }
        out += "+\n";
        for (int y = 0; y < groesse; y++) {
            out +="|";
            for (int x = 0; x < groesse; x++) {
                out += zeichen[spielfeldflaeche[x][y].gibInhalt()];
            }
            out += "|\n";
        }
        out += "+";
        for (int x=0; x < groesse; x++) {
            out += "-";
        }
        out += "+\n";
        return out;
    }  
}
