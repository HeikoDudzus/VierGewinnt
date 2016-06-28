import javax.swing.*;
import java.util.regex.*;

/**
 * Write a description of class GameClient here.
 * 
 * @author Heiko Dudzus (Die Hinterbänkler)
 * @version 2016-06-24
 */

public class GameClient extends Client
{
    // Server gibt regelmäßig Meldungen über den Zustand des eigenen Spielers
    private boolean aktiv;
    private boolean won;
    private boolean lost;
    private boolean unentschieden;
    private JButton[][] buttons;
    private JTextArea ausgabe;
    private JLabel eigenerName;
    private JLabel gegnerName;
    private JLabel gewonnen;
    private String symbol;
    private String name;
    private String gegner;

    /**
     * Constructor for objects of class GameClient
     */
    public GameClient(String pIPAdresse, int pPortNr, JButton[][] pButtons, JTextArea pAusgabe,
    JLabel pEigenerName, JLabel pGegnerName, JLabel pGewonnen)
    {
        super(pIPAdresse, pPortNr);
        buttons = pButtons;
        ausgabe = pAusgabe;
        eigenerName = pEigenerName;
        gegnerName = pGegnerName;
        gewonnen = pGewonnen;
        // aktiv = false;
        //won = false;
        //lost = false;
        //unentschieden = false;
    }

    public void processMessage(String pMessage) {
        // Nachricht für Mensch oder Maschine?
        if (pMessage != null && !pMessage.startsWith("+")) {
            ausgabe.append(pMessage+"\n");
        } else {
            String[] stuecke = pMessage.split(" ");
            if (stuecke.length == 1) {
                if (stuecke[0].equals("+ACTIVE")) ;
                if (stuecke[0].equals("+PASSIVE")) ;
                if (stuecke[0].equals("+WON")) {
                    gewonnen.setText("Gewonnen! Herzlichen Glückwunsch");
                }
                if (stuecke[0].equals("+LOST")) {
                    gewonnen.setText("Leider verloren!");
                }
                if (stuecke[0].equals("+TIED")) {
                    gewonnen.setText("Unentschieden!");
                }
            } else if (stuecke.length == 2) {
                if (stuecke[0].equals("+NICKIS")) {
                    name = stuecke[1];
                    eigenerName.setText(name);
                }
                if (stuecke[0].equals("+GAMEWITH")) {
                    gegner = stuecke[1];
                    gegnerName.setText(gegner);
                }
                if (stuecke[0].equals("+SYMBOL")) symbol = stuecke[1];
            } else if (stuecke.length == 4) {
                if (stuecke[0].equals("+SET")) {
                    // Setzen des Feldes erfolgreich
                    String s = stuecke[1]; // Symbol
                    int i = Integer.parseInt(stuecke[2]);
                    int j = Integer.parseInt(stuecke[3]);
                    buttons[i][j].setText(s);
                    //aktiv = !aktiv; // Jetzt wechselt der aktive Spieler
                }
            }
            // Ausgabe von Nachrichten im GUI
            ausgabe.append(pMessage+"\n");
        }
    }

    public void waehleFeld(int pI, int pJ) {
        super.send("MOVE " + pI + " " + pJ);
    }

    public void fordereNeuesSpiel() {
        super.send("NEW");
    }

    public void beenden() {
        super.send("QUIT");
    }

    public void setzeNamen(String pName) {
        // Sonderzeichen entfernen mit regex
        super.send("NICK " + pName);
    }

    public boolean gibAktiv() {
        return aktiv;
    }
}