/**
 * @author Gerrit (Die Hinterbänkler)
 * @version 2016-06-12
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener
{
    private JLabel ueberschrift, spielerAktiv1, spielerAktiv2, gewonnen, labelRed;
    private JButton[][] buttons;
    private JButton neuesSpiel;
    private VierGewinntSpiel vierGewinntSpiel;
    //private GameClient gameClient;
    private boolean buttonsFreiGegeben;

    public GUI()
    {
        super("Vier gewinnt!");
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setSize(850, 850);
        setBackground(Color.BLUE);

        //Erstellung der festen Texte (Labels).
        ueberschrift = new JLabel("Vier Gewinnt");
        ueberschrift.setBounds(400, 15, 300, 60);
        ueberschrift.setFont(new Font("Arial", Font.BOLD, 15));
        add(ueberschrift);
        spielerAktiv1 = new JLabel("Der folgende Spieler ist an der Reihe: X");     
        spielerAktiv1.setBounds(160, 70, 300, 50);
        spielerAktiv1.setFont(new Font("Arial", Font.ITALIC, 13));
        add(spielerAktiv1);

        buttons = new JButton[7][7];
        //Label für die Anzeige eines Gewinns.
        gewonnen = new JLabel("");
        gewonnen.setBounds(160, 120, 400, 50);
        gewonnen.setForeground(Color.RED);
        add(gewonnen);

        //Button für ein neues Spiel.
        neuesSpiel = new JButton("Neues Spiel");
        neuesSpiel.setBounds(680, 710, 150, 50);
        neuesSpiel.addActionListener(this);
        add(neuesSpiel);

        //Erstellung der Buttons fuer die 49 Felder.
        int a=0;
        int b=0;
        for (int y=200; y<=680; y=y+80)
        {
            a=0;
            for (int i=100; i<=580; i=i+80)
            {
                buttons[a][b] = new JButton("");
                buttons[a][b].setBounds(i, y, 80, 80);
                buttons[a][b].addActionListener(this);
                add(buttons[a][b]); 
                a++;
            } 
            b++;
        }

        this.repaint();
        vierGewinntSpiel = new VierGewinntSpiel();
        buttonsFreiGegeben = true;
    }

    private void leereButtonBeschriftung()
    {
        for(int i=0; i<7; i++)
        {
            for(int j=0; j<7; j++)
            {
                buttons[i][j].setText("");
            }
        }
        repaint();
    }

    public void actionPerformed(ActionEvent k)
    {
        if(k.getSource()==neuesSpiel && buttonsFreiGegeben==false)
        {
            vierGewinntSpiel.setzeNeuesSpiel();
            leereButtonBeschriftung();
            gewonnen.setText("");
            buttonsFreiGegeben = true;
        }

        if(buttonsFreiGegeben == true)
        {
            for(int i=0; i<7; i++)
            {
                for(int j=0; j<7; j++)
                {
                    if(k.getSource()==buttons[i][j])
                    {
                        //Weil der aktive Spieler bei der Methode setzeSymbol(...) ggf. direkt den aktiven Spieler aendert, muss dieser zuvor in aktiverSpielerZuDiesemZeitpunkt gespeichert werden.
                        String aktiverSpielerZuDiesemZeitpunkt = vierGewinntSpiel.gibAktivenSpieler();
                        if(vierGewinntSpiel.setzeSymbol(i, j))
                        {
                            buttons[i][j].setText(aktiverSpielerZuDiesemZeitpunkt);
                            spielerAktiv1.setText("Der folgende Spieler ist an der Reihe: " + vierGewinntSpiel.gibAktivenSpieler());
                        }
                        //i und j werden hochgesetzt, damit die for-Schleife ggf. nicht weiterläuft.
                        i=7;
                        j=7;
                    }
                }
            }

            if(vierGewinntSpiel.spielerGewonnen("X"))
            {
                gewonnen.setText("SPIELER X HAT GEWONNEN! HERZLICHEN GLÜCKWÜNSCH!!!");
                buttonsFreiGegeben = false;
            }
            else if(vierGewinntSpiel.spielerGewonnen("O"))
            {
                gewonnen.setText("SPIELER O HAT GEWONNEN! HERZLICHEN GLÜCKWÜNSCH!!!");
                buttonsFreiGegeben = false;
            }
            else if(vierGewinntSpiel.gibAlleFelderVoll())
            {
                gewonnen.setText("UNENTSCHIEDEN!");
                buttonsFreiGegeben = false;
            }
        }
    }
}

