/**
 * @author Gerrit(Spiellogik), Heiko (Änderungen für Netzwerk) (Die Hinterbänkler)
 * @version 2016-06-12
 */

public class VierGewinntSpiel implements Zustand
{
    private Spielfeld spielfeld;
    private String aktiverSpieler;
    private List<Spieler> spieler;
    private Spieler spieler1;
    private Spieler spieler2;

    public VierGewinntSpiel()
    {
        spielfeld = new Spielfeld();
        aktiverSpieler = "X";

    }

    public VierGewinntSpiel(Spieler pSpieler1, Spieler pSpieler2)
    {
        spielfeld = new Spielfeld();
        spieler = new List<Spieler>();
        spieler.append(pSpieler1);
        spieler.append(pSpieler2);
        aktiverSpieler = "X";
        // if (spieler1.gibZustand() == PASSIVE) spieler1.setzeZustand(ACTIVE);
    }

    //obsolet
    /*public void loescheSpielerNachNamen(String pName) {
    spieler.toFirst();
    while (spieler.hasAccess()) {
    if (pName.equals(spieler.getContent().gibName())) spieler.remove();
    spieler.next();
    }
    }*/

    public void loescheSpieler(Spieler pClient) {
        spieler.toFirst();
        while (spieler.hasAccess()) {
            if (pClient == spieler.getContent()) spieler.remove();
            spieler.next();
        }
    }

    public boolean pruefeSpieler(Spieler pSpieler) {
        spieler.toFirst();
        while (spieler.hasAccess()) {
            if (pSpieler == spieler.getContent()) return true;
            spieler.next();
        }
        return false;
    }

    public Spieler gibGegenspieler(Spieler pSpieler) {
        spieler.toFirst();
        while (spieler.hasAccess()) {
            Spieler s = spieler.getContent();
            if (pSpieler != s) return s;
            spieler.next();
        }
        return null;
    }

    public boolean beideSpielerWeg() {
        return spieler.isEmpty();
    }

    //     public void setzeSpieler1(Spieler pSpieler) {
    //         spieler1 = pSpieler;
    //     }
    //     
    //     public void setzeSpieler2(Spieler pSpieler) {
    //         spieler2 = pSpieler;
    //     }

    public boolean setzeSymbol(int pX, int pY)
    {
        if (pX >= 0 && pX < 7 && pY >= 0 && pY < 7) {
            if (aktiverSpieler.equals("X"))
            {
                if(pY==6 && spielfeld.gibZustandDesFeldes(pX, pY).equals("leer"))
                {          
                    spielfeld.setzeXaufFeld(pX, pY);
                    aktiverSpieler = "O";
                    return true;
                }
                else if(spielfeld.gibZustandDesFeldes(pX, pY).equals("leer") && !spielfeld.gibZustandDesFeldes(pX, pY+1).equals("leer"))
                {
                    spielfeld.setzeXaufFeld(pX, pY);
                    aktiverSpieler = "O";
                    return true;
                }
                else 
                {
                    return false;
                }
            }
            else
            {
                if(pY==6 && spielfeld.gibZustandDesFeldes(pX, pY).equals("leer"))
                {          
                    spielfeld.setzeOaufFeld(pX, pY);
                    aktiverSpieler = "X";
                    return true;
                }
                else if(spielfeld.gibZustandDesFeldes(pX, pY).equals("leer") && !spielfeld.gibZustandDesFeldes(pX, pY+1).equals("leer"))
                {
                    spielfeld.setzeOaufFeld(pX, pY);
                    aktiverSpieler = "X";
                    return true;
                }
                else
                {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /*public String gibAktivenSpieler()
    {
    return aktiverSpieler;
    }*/

    /*public Spieler gibAktivenSpieler2() {
    Spieler out = null;
    if (spieler1.gibZustand() == ACTIVE) out = spieler1;
    if (spieler2.gibZustand() == ACTIVE) out = spieler2;
    return out;
    }*/

    /*public Spieler gibPassivenSpieler() {
    spieler.toFirst();
    while (spieler.hasAccess()) {
    Spieler sp = spieler.getContent();
    if (sp.gibZustand() == PASSIVE) return sp;
    spieler.next();
    }
    return null;
    }*/

    public void setzeNeuesSpiel()
    {
        spielfeld.setzeNeuesSpielfeld();
        aktiverSpieler = "X";
    }

    public boolean gibAlleFelderVoll()
    {
        return spielfeld.gibIstSpielfeldVoll();
    }

    public boolean spielerGewonnen(String pSpieler)
    {
        boolean hatErGewonnen = false;

        if(vierInEinerZeile(pSpieler))
        {
            hatErGewonnen = true;
        }

        if(vierInEinerSpalte(pSpieler))
        {
            hatErGewonnen = true;
        }

        if(vierInEinerDiagonalen(pSpieler))
        {
            hatErGewonnen = true;
        }

        return hatErGewonnen;
    }

    public boolean vierInEinerZeile(String pSpieler)
    {
        boolean vierVorhanden = false;

        for(int i=0; i<7; i++)
        {
            if (vierInEinerBestimmtenZeile(i, pSpieler))
            {
                vierVorhanden = true;
            }
        }

        return vierVorhanden;
    }

    private boolean vierInEinerBestimmtenZeile(int pZeile, String pSpieler)
    {
        boolean vierVorhanden = false;
        int anzahlDerMarkierungen = 0;

        for(int j=0; j<7; j++)
        {
            if(spielfeld.gibZustandDesFeldes(j, pZeile).equals(pSpieler))
            {
                anzahlDerMarkierungen++;
                if(anzahlDerMarkierungen>3)
                {
                    vierVorhanden = true;
                }
            }
            else
            {
                anzahlDerMarkierungen = 0;
            }
        }

        return vierVorhanden;
    }

    public boolean vierInEinerSpalte(String pSpieler)
    {
        boolean vierVorhanden = false;

        for(int i=0; i<7; i++)
        {
            if (vierInEinerBestimmtenSpalte(i, pSpieler))
            {
                vierVorhanden = true;
            }
        }

        return vierVorhanden;
    }

    private boolean vierInEinerBestimmtenSpalte(int pSpalte, String pSpieler)
    {
        boolean vierVorhanden = false;
        int anzahlDerMarkierungen = 0;

        for(int i=0; i<7; i++)
        {
            if(spielfeld.gibZustandDesFeldes(pSpalte, i).equals(pSpieler))
            {
                anzahlDerMarkierungen++;
                if(anzahlDerMarkierungen>3)
                {
                    vierVorhanden = true;
                }
            }
            else
            {
                anzahlDerMarkierungen = 0;
            }
        }

        return vierVorhanden;
    }

    public boolean vierInEinerDiagonalen(String pSpieler)
    {
        boolean vierVorhanden = false;
        int anzahlDerMarkierungen = 0;

        //A. Prüfen der Diagnonalen, die nach rechts gehen.
        //A1. Immer eine Zeile am linken Rand tiefer gehen und Diagonale prüfen.
        for(int i=3; i<7; i++)
        {
            if(eineDiagonaleRechtsPruefen(0, i, pSpieler))
            {
                vierVorhanden = true;
            }
        }
        //A2. Immer eine Spalte am unteren Rand weiter nach rechts gehen und die Diagonale prüfen.
        for(int i=1; i<4; i++)
        {
            if(eineDiagonaleRechtsPruefen(i, 6, pSpieler))
            {
                vierVorhanden = true;
            }
        }

        //B. Prüfen der Diagonalen, die nach links gehen.
        //B1. Immer eine Zeile am rechten Rand tiefer gehen und Diagonale prüfen.
        for(int i=3; i<7; i++)
        {
            if(eineDiagonaleLinksPruefen(6, i, pSpieler))
            {
                vierVorhanden = true;
            }
        }
        //B2. Immer eine Spalte am unteren Rand weiter nach links gehen und die Diagonale prüfen.
        for(int i=5; i>2; i--)
        {
            if(eineDiagonaleLinksPruefen(i, 6, pSpieler))
            {
                vierVorhanden = true;
            }
        }

        return vierVorhanden;
    }

    private boolean eineDiagonaleRechtsPruefen(int pStartX, int pStartY, String pSpieler)
    {
        boolean vierVorhanden = false;
        int anzahlDerMarkierungen = 0;

        while(pStartY>-1 && pStartX<7)
        {
            if(spielfeld.gibZustandDesFeldes(pStartX, pStartY).equals(pSpieler))
            {
                anzahlDerMarkierungen++;
                if(anzahlDerMarkierungen>3)
                {
                    vierVorhanden = true;
                }
            }
            else
            {
                anzahlDerMarkierungen = 0;
            }

            pStartX++;
            pStartY--;
        }

        return vierVorhanden;
    }

    private boolean eineDiagonaleLinksPruefen(int pStartX, int pStartY, String pSpieler)
    {
        boolean vierVorhanden = false;
        int anzahlDerMarkierungen = 0;

        while(pStartY>-1 && pStartX>-1)
        {
            if(spielfeld.gibZustandDesFeldes(pStartX, pStartY).equals(pSpieler))
            {
                anzahlDerMarkierungen++;
                if(anzahlDerMarkierungen>3)
                {
                    vierVorhanden = true;
                }
            }
            else
            {
                anzahlDerMarkierungen = 0;
            }

            pStartX--;
            pStartY--;
        }

        return vierVorhanden;
    }

    /*public Spieler gibSpieler1() {
    return spieler1;
    }*/

    /*public Spieler gibSpieler2() {
    return spieler2;
    }*/

    public String toString() {
        return spielfeld.toString();
    }
}