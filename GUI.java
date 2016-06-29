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
    private JLabel aktiv;
    private SpielButton[][] buttons;
    private JButton neuesSpiel;
    private VierGewinntSpiel vierGewinntSpiel;
    private boolean buttonsFreiGegeben;

    private GameClient gameClient;
    private JTextField port, ip;
    private JTextArea nachrichten;

    private JTextField nicknameEingabefenster;
    private JButton nicknameButton;
    private JLabel nickname;

    private JTextField portEingabefenster, ipEingabefenster;
    private JButton ipPortConnect;
    private JLabel portSchrift, ipSchrift;

    public GUI()
    {
        super("Vier gewinnt!");
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setSize(860, 1100);
        setBackground(Color.BLUE);

        //Erstellung der festen Texte (Labels).
        ueberschrift = new JLabel("Vier Gewinnt");
        ueberschrift.setBounds(400, 15, 300, 60);
        ueberschrift.setFont(new Font("Arial", Font.BOLD, 15));
        add(ueberschrift);
        spielerAktiv1 = new JLabel("Sie müssen sich verbinden und einen Nickname wählen.");     
        spielerAktiv1.setBounds(160, 70, 350, 50);
        spielerAktiv1.setFont(new Font("Arial", Font.ITALIC, 13));
        add(spielerAktiv1);

        // JTextField für IP und Port hier erzeugen

        // Anzeige von Nachrichten des Gameservers
        nachrichten = new JTextArea();
        JScrollPane laufleiste = new JScrollPane(nachrichten);
        laufleiste.setBounds(100, 850, 560, 100);
        nachrichten.setEditable(false);
        add(laufleiste);

        // Label, das anzeigt, ob der Spieler aktiv ist
        aktiv = new JLabel("");
        aktiv.setBounds(20,20,50,50);
        aktiv.setOpaque(true);
        aktiv.setBackground(Color.BLUE);
        add(aktiv);

        buttons = new SpielButton[7][7];
        //Label für die Anzeige eines Gewinns.
        gewonnen = new JLabel("");
        gewonnen.setBounds(160, 120, 400, 50);
        gewonnen.setForeground(Color.RED);
        add(gewonnen);

        //Button für ein neues Spiel.
        neuesSpiel = new JButton("Neues Spiel");
        neuesSpiel.setBounds(680, 60, 150, 50);
        neuesSpiel.addActionListener(this);
        add(neuesSpiel);

        //Erstellung der Buttons fuer die 49 Felder.
        int a=0;
        int b=0;
        for (int y=150; y<=630; y=y+80)
        {
            a=0;
            for (int i=150; i<=630; i=i+80)
            {
                buttons[a][b] = new SpielButton();
                buttons[a][b].setBounds(i, y, 80, 80);
                //buttons[a][b].addActionListener(this);
                buttons[a][b].addMouseListener(new MeinMouseAdapter(a,b));
                add(buttons[a][b]); 
                a++;
            } 
            b++;
        }

        nickname = new JLabel("2. Nickname:");
        nickname.setBounds(100, 810, 80, 30);
        add(nickname);
        nicknameEingabefenster = new JTextField();
        nicknameEingabefenster.setBounds(190, 810, 100, 30);
        add(nicknameEingabefenster);
        nicknameButton = new JButton("send");
        nicknameButton.setBounds(410, 810, 90, 30);
        add(nicknameButton);
        nicknameButton.addActionListener(this);
        ipSchrift = new JLabel("1. IP:");
        ipSchrift.setBounds(100, 770, 30, 30);
        add(ipSchrift);
        ipEingabefenster = new JTextField("127.0.0.1");
        ipEingabefenster.setBounds(140, 770, 100, 30);
        add(ipEingabefenster);
        portSchrift = new JLabel("Port:");
        portSchrift.setBounds(250, 770, 40, 30);
        add(portSchrift);
        portEingabefenster = new JTextField("10000");
        portEingabefenster.setBounds(290, 770, 100, 30);
        add(portEingabefenster);
        ipPortConnect = new JButton("connect");
        ipPortConnect.setBounds(410, 770, 90, 30);
        add(ipPortConnect);
        ipPortConnect.addActionListener(this);

        this.repaint();
        vierGewinntSpiel = new VierGewinntSpiel();
        buttonsFreiGegeben = true;
    }

    private void verbinde(String pIP, int pPort) {
        gameClient = new GameClient(pIP, pPort, buttons, nachrichten, spielerAktiv1, spielerAktiv2, gewonnen, aktiv);
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
        repaint();
    }

    public void actionPerformed(ActionEvent k)
    {
        if(k.getSource()==neuesSpiel && buttonsFreiGegeben==false)
        {
            vierGewinntSpiel.setzeNeuesSpiel();
            // gameClient.fordereNeuesSpiel();
            leereButtonBeschriftung();
            gewonnen.setText("");
            buttonsFreiGegeben = true;
        }

        if(k.getSource() == nicknameButton)
        {
            gameClient.setzeNamen("" + nicknameEingabefenster.getText());
        }
        else if(k.getSource()== ipPortConnect)
        {
            verbinde("" + ipEingabefenster.getText(), Integer.parseInt(portEingabefenster.getText()));
        }

        //         if(buttonsFreiGegeben == true)
        //         {
        //             for(int i=0; i<7; i++)
        //             {
        //                 for(int j=0; j<7; j++)
        //                 {
        //                     if(k.getSource()==buttons[i][j])
        //                     {
        //                         //Weil der aktive Spieler bei der Methode setzeSymbol(...) ggf. direkt den aktiven Spieler aendert, muss dieser zuvor in aktiverSpielerZuDiesemZeitpunkt gespeichert werden.
        //                         String aktiverSpielerZuDiesemZeitpunkt = vierGewinntSpiel.gibAktivenSpieler();
        //                         if(vierGewinntSpiel.setzeSymbol(i, j))
        //                         {
        //                             buttons[i][j].setText(aktiverSpielerZuDiesemZeitpunkt);
        //                             spielerAktiv1.setText("Der folgende Spieler ist an der Reihe: " + vierGewinntSpiel.gibAktivenSpieler());
        //                         }
        //                         //i und j werden hochgesetzt, damit die for-Schleife ggf. nicht weiterläuft.
        //                         i=7;
        //                         j=7;
        //                     }
        //                 }
        //             }
        // 
        //             if(vierGewinntSpiel.spielerGewonnen("X"))
        //             {
        //                 gewonnen.setText("SPIELER X HAT GEWONNEN! HERZLICHEN GLÜCKWÜNSCH!!!");
        //                 buttonsFreiGegeben = false;
        //             }
        //             else if(vierGewinntSpiel.spielerGewonnen("O"))
        //             {
        //                 gewonnen.setText("SPIELER O HAT GEWONNEN! HERZLICHEN GLÜCKWÜNSCH!!!");
        //                 buttonsFreiGegeben = false;
        //             }
        //             else if(vierGewinntSpiel.gibAlleFelderVoll())
        //             {
        //                 gewonnen.setText("UNENTSCHIEDEN!");
        //                 buttonsFreiGegeben = false;
        //             }
        //         }
    }

    private class MeinMouseAdapter extends MouseAdapter {
        int x,y;
        public MeinMouseAdapter(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void mouseClicked(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON1) {
                gameClient.waehleFeld(x,y);
            }
        }
    }
}
