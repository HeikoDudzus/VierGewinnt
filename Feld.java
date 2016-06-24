public class Feld
{
    private int zustand;

    public Feld()
    {
        zustand = 0;
    }

    public void setzeLeer()
    {
        zustand = 0;
    }
    
    public void setzeX()
    {
        zustand = 1;
    }

    public void setzeO()
    {
        zustand = 2;
    }

    public String gibZustand()
    {
        if(zustand==0)
        {
            return "leer";
        }
        else if (zustand==1)
        {
            return "X";
        }
        else if (zustand==2)
        {
            return "O";
        }
        else
        {
            return "panic";
        }
    }
}
