import javax.swing.*;
import java.awt.*;
import java.util.regex.*;

/**
 * Write a description of class GameClient here.
 * 
 * @author Heiko Dudzus (Die Hinterbaenkler)
 * @version 2016-06-24
 */

public class GameClient extends Client
{
    // Server gibt regelmaessig Meldungen ueber den Zustand des eigenen Spielers
    //private boolean aktiv;
    private boolean won;
    private boolean lost;
    private boolean unentschieden;
    private SpielButton[][] buttons;
    private JTextArea ausgabe;
    private JLabel eigenerName;
    private JLabel gegnerName;
    private JLabel gewonnen;
    private SpielLabel bAktiv;
    private String symbol;
    private String name;
    private String gegner;

    /**
     * Constructor for objects of class GameClient
     * @param pIPAdresse IP-Adresse bzw. Domain des Servers
     * @param pPortNr Portnummer des Sockets
     * @param pButtons Array von Buttons, das die Spielfelder darstellt
     * @param pAusgabe Textfeld, in das die Server-Meldungen geschrieben werden
     * @param pEigenerName Feld, in das der Client den eigenen Namen schreibt
     * @param pGegnerName Feld, in das der Client den Namen des Gegners schreibt
     * @param pGewonnen Feld, in das der Client den Gewinner-Status schreibt
     * @param pAktiv Label, das anzeigt, ob der Spieler an der Reihe ist
     */
    public GameClient(String pIPAdresse, int pPortNr, SpielButton[][] pButtons, JTextArea pAusgabe,
    JLabel pEigenerName, JLabel pGegnerName, JLabel pGewonnen, SpielLabel pAktiv)
    {
        super(pIPAdresse, pPortNr);
        buttons = pButtons;
        ausgabe = pAusgabe;
        eigenerName = pEigenerName;
        gegnerName = pGegnerName;
        gewonnen = pGewonnen;
        bAktiv = pAktiv;
        // aktiv = false;
        //won = false;
        //lost = false;
        //unentschieden = false;
    }

    /**
     * Eine Nachricht vom Server wird verarbeitet.
     * @param pMessage Nachricht, die vom Server kommt
     */
    public void processMessage(String pMessage) {
        // Nachricht für Mensch oder Maschine?
        if (pMessage != null && !pMessage.startsWith("+") && !pMessage.startsWith("-")) {
            ausgabe.append(pMessage+"\n");
        } else {
            String[] stuecke = pMessage.split(" ");
            if (stuecke.length == 1) {
                if (stuecke[0].equals("+WAIT")) {
                    leereButtonBeschriftung();
                    gewonnen.setText("");
                    bAktiv.setGameColor(Color.GRAY);
                }
                if (stuecke[0].equals("+ACTIVE")) {
                    bAktiv.setGameColor(Color.GREEN);
                }
                if (stuecke[0].equals("+PASSIVE")) {
                    bAktiv.setGameColor(Color.RED);
                }
                if (stuecke[0].equals("+WON")) {
                    gewonnen.setText("Gewonnen! Herzlichen Gl�ckwunsch");
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
                    //gegnerName.setText(gegner);
                }
                if (stuecke[0].equals("+SYMBOL")) symbol = stuecke[1];
            } else if (stuecke.length == 4) {
                if (stuecke[0].equals("+SET")) {
                    // Setzen des Feldes erfolgreich
                    String s = stuecke[1]; // Symbol
                    int i = Integer.parseInt(stuecke[2]);
                    int j = Integer.parseInt(stuecke[3]);
                    if (s.equals("X")) {
                        buttons[i][j].setText(s);
                        buttons[i][j].setGameColor(Color.RED);
                    } else if (s.equals("O")) {
                        buttons[i][j].setText(s);
                        buttons[i][j].setGameColor(Color.ORANGE);
                    }
                    //aktiv = !aktiv; // Jetzt wechselt der aktive Spieler
                }
            }
            // Ausgabe von Nachrichten im GUI
            ausgabe.append(pMessage+"\n");
        }
    }

    /**
     * Ein gew�hltes Spielfeld wird gemaess Protokoll dem Server gemeldet.
     * @param pI Spalte (zwischen 0 und 6)
     * @param pJ Zeile (zwischen 0 und 6)
     */
    public void waehleFeld(int pI, int pJ) {
        super.send("MOVE " + pI + " " + pJ);
    }

    /**
     * Ein neues Spiel wird beim Server gemaess Protokoll angefordert.
     */
    public void fordereNeuesSpiel() {
        super.send("NEW");
    }

    /**
     * Der Client meldet sich vom Server ab.
     */
    public void beenden() {
        super.send("QUIT");
    }

    private void leereButtonBeschriftung()
    {
        for(int i=0; i<7; i++)
        {
            for(int j=0; j<7; j++)
            {
                buttons[i][j].setText("");
                buttons[i][j].setGameColor(java.awt.Color.BLACK);
            }
        }
        //repaint();
    }

    /**
     * Der Client fordert einen Nickname an.
     * @param pName angeforderter Nickname
     */
    public void setzeNamen(String pName) {
        // Sonderzeichen entfernen mit regex
        super.send("NICK " + pName);
    }
}
