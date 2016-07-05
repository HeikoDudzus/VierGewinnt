/**
 * Klasse GameServer.
 * 
 * @author Heiko Dudzus, Gerrit (Die Hinterbänkler)
 * @version 2016-04-27
 */

public class GameServer extends Server implements Zustand
{
    //private DBVierGewinnt db;
    private List<Spieler> spielerListe;
    private Queue<Spieler> warteschlange;
    private List<VierGewinntSpiel> spiele;

    /**
     * Konstruktor fuer Objekte der Klasse GameServer
     */
    public GameServer(int port)
    {
        super(port);
        spielerListe = new List<Spieler>();
        warteschlange = new Queue<Spieler>();
        spiele = new List<VierGewinntSpiel>();
        System.out.println("Der Server lauscht an Port "+port+"!");
    }

    public void processNewConnection(String pClientIP, int pClientPort) {
        send(pClientIP, pClientPort, "Herzlich Willkommen auf dem Vier-Gewinnt-Gameserver!");
        send(pClientIP, pClientPort, "\"QUIT\" beendet die Verbindung!");
        System.out.println(pClientIP + " : " + pClientPort + " hat sich eingewaehlt.");
        Spieler spieler = new Spieler(pClientIP, pClientPort);
        spielerListe.append(spieler);
        send(pClientIP, pClientPort, "Waehlen Sie einen Nickname mit NICK <name>");
    }

    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        Spieler spieler = sucheClientNachIPUndPort(pClientIP, pClientPort);
        //System.out.println(spieler.gibName());
        if (spieler != null){
            int zustand = spieler.gibZustand();
            switch (zustand) {
                case NICKNAME : processNickname(spieler, pMessage); break;
                case WAIT : processWait(spieler, pMessage); break;
                case PASSIVE : processPassive(spieler, pMessage); break;
                case ACTIVE : processActive(spieler, pMessage); break;
                case OVER : processOver(spieler, pMessage); break;
                default: System.out.println("Fehler, Client-Zustand "+ zustand+" nicht definiert.");
                System.out.println(spieler);
            }
        } else {
            System.out.println("PANIC - kein Client mit diesen Daten.");
        }
    }

    private void processNickname(Spieler pClient, String pMessage){
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        if (stuecke.length == 2) {
            // NICK angefordert?
            if (stuecke[0].equals("NICK")){
                // Ist ein Client mit dem gewuenschten Namen noch nicht vorhanden?
                if (!nameVorhanden(stuecke[1]) && nameErlaubt(stuecke[1])){
                    pClient.setzeName(stuecke[1]);
                    pClient.setzeZustand(WAIT);
                    send(clientIP, clientPort, "+NICKIS " + stuecke[1]);
                    warteschlange.enqueue(pClient);
                    // Wenn schon zwei Spieler angemeldet sind, soll das Spiel gestartet werden
                    starteSpielWennMoeglich();
                } else {
                    send(clientIP, clientPort, "Name invalid");
                }
            }
        } else if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "+OK see you soon");
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "ERR unknown command");
        } 
    }

    private void processWait(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        }
    }

    private void processPassive(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        VierGewinntSpiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        if (pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            if (gegenspieler != null) {
                send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
                gegenspieler.setzeZustand(OVER);
                registriereGewinnerInDB(gegenspieler);
            }
            s.loescheSpieler(pClient);
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "-ERR it is not your turn");
        }
    }

    private void processActive(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        String symbol = pClient.gibSymbol();
        VierGewinntSpiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        boolean validInt = true;
        int i=0, j=0;
        if (stuecke.length == 3) {
            if (stuecke[0].equals("MOVE")) {
                try {
                    i = Integer.parseInt(stuecke[1]);
                    j = Integer.parseInt(stuecke[2]);
                } catch (Exception e) {
                    System.out.println("Konnte keine Integer parsen!");
                    validInt = false;
                }
                if (s != null && validInt) {
                    boolean status = s.setzeSymbol(i,j);
                    if (status) {
                        send(clientIP, clientPort, "+SET "+symbol+" "+i+" "+j);
                        send(clientIP, clientPort, "+PASSIVE");
                        //send(clientIP, clientPort, s.toString());
                        pClient.setzeZustand(PASSIVE);
                        send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+SET "+symbol+" "+i+" "+j);
                        send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+ACTIVE");
                        //send(gegenspieler.gibIP(), gegenspieler.gibPort(), s.toString());
                        gegenspieler.setzeZustand(ACTIVE);
                        if (s.spielerGewonnen(pClient.gibSymbol())) {
                            send(clientIP, clientPort, "+WON");
                            registriereGewinnerInDB(pClient);
                            send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+LOST");
                            pClient.setzeZustand(OVER);
                            gegenspieler.setzeZustand(OVER);
                        } else if (s.gibAlleFelderVoll()) {
                            send(clientIP, clientPort, "+TIED");
                            send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+TIED");
                            pClient.setzeZustand(OVER);
                            gegenspieler.setzeZustand(OVER);
                        }
                    } else {
                        send(clientIP, clientPort, "move not possible");
                    }
                }
            }
        } else if (stuecke.length == 1) {
            if (pMessage.equals("QUIT")) {
                send(clientIP, clientPort, "+OK see you soon");
                if (gegenspieler != null) {
                    send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
                    gegenspieler.setzeZustand(OVER);
                    registriereGewinnerInDB(gegenspieler);
                }
                s.loescheSpieler(pClient);
                if (s.beideSpielerWeg()) loescheSpielAusListe(s);
                loescheSpieler(pClient);
                closeConnection(clientIP, clientPort);
            } else {
                send(clientIP, clientPort, "-ERR unknown command");
            }
        } else {
            send(clientIP, clientPort, "-ERR unknown command");
        }
    }

    private void processOver(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        VierGewinntSpiel s = gibSpielNachSpieler(pClient);
        Spieler gegenspieler = s.gibGegenspieler(pClient);
        if (pMessage.equals("NEW")) {
            // Spieler in Wartezustand
            send(clientIP, clientPort, "Waiting for a new game");
            send(clientIP, clientPort, "+WAIT");
            pClient.setzeZustand(WAIT);
            warteschlange.enqueue(pClient);
            // Spieler aus seinem Spiel nehmen
            s.loescheSpieler(pClient);
            // Liste der Spiele aktualisieren
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            starteSpielWennMoeglich();
        } else if(pMessage.equals("QUIT")) {
            send(clientIP, clientPort, "+OK see you soon");
            s.loescheSpieler(pClient);
            if (s.beideSpielerWeg()) loescheSpielAusListe(s);
            loescheSpieler(pClient);
            closeConnection(clientIP, clientPort);
        } else {
            send(pClient.gibIP(), pClient.gibPort(), "-ERR unknown command");
        }
    }

    private void loescheSpielAusListe(VierGewinntSpiel pSpiel) {
        spiele.toFirst();
        while (spiele.hasAccess()) {
            if (pSpiel == spiele.getContent()) spiele.remove();
            spiele.next();
        }
    }

    private void loescheSpieler(Spieler pSpieler) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler == pSpieler) {
                spielerListe.remove();
            }
            spielerListe.next();
        }
        if (warteschlange.front() == pSpieler) warteschlange.dequeue();
    }

    private void loescheClientNachIPUndPort(String pClientIP, int pClientPort){
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler.vergleicheIPUndPort(spieler.gibIP(), spieler.gibPort())) {
                spielerListe.remove();
            }
            spielerListe.next();
        }
    }

    private Spieler sucheClientNachIPUndPort(String pClientIP, int pClientPort){
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler.vergleicheIPUndPort(pClientIP, pClientPort)) {
                return spieler;
            }
            spielerListe.next();
        }
        return null;
    }

    private boolean nameVorhanden(String pName) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (pName.equals(spieler.gibName())) {
                return true;
            }
            spielerListe.next();
        }
        return false;
    }

    private Spieler gibSpielerNachNamen(String pName) {
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (pName.equals(spieler.gibName())) {
                return spieler;
            }
            spielerListe.next();
        }
        return null;
    }

    public void processClosedConnection(String pClientIP, int pClientPort) {
        System.out.println(pClientIP + " : " + pClientPort + " hat sich abgemeldet.");
        /*Spieler spieler = sucheClientNachIPUndPort(pClientIP, pClientPort);
        VierGewinntSpiel s = gibSpielNachSpieler(spieler);
        Spieler gegenspieler = s.gibGegenspieler(spieler);
        send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+WON");
        registriereGewinnerInDB(gegenspieler);
        s.loescheSpieler(spieler);
        if (s.beideSpielerWeg()) loescheSpielAusListe(s);
        loescheSpieler(spieler);*/
    }

    private void starteSpielWennMoeglich(){
        Spieler spieler1 = warteschlange.front();
        warteschlange.dequeue();
        if (warteschlange.isEmpty()) {
            warteschlange.enqueue(spieler1);
        } else {
            Spieler spieler2 = warteschlange.front();
            warteschlange.dequeue();
            spieler1.setzeSymbol("X");
            spieler2.setzeSymbol("O");
            VierGewinntSpiel s = new VierGewinntSpiel(spieler1, spieler2);
            spiele.append(s);
            spieler1.setzeZustand(ACTIVE);
            spieler2.setzeZustand(PASSIVE);
            send(spieler1.gibIP(), spieler1.gibPort(), "+GAMEWITH "+spieler2.gibName());
            send(spieler1.gibIP(), spieler1.gibPort(), "+SYMBOL "+spieler1.gibSymbol());
            send(spieler2.gibIP(), spieler2.gibPort(), "+GAMEWITH "+spieler1.gibName());
            send(spieler2.gibIP(), spieler2.gibPort(), "+SYMBOL "+spieler2.gibSymbol());
            send(spieler1.gibIP(), spieler1.gibPort(), "+ACTIVE");
            send(spieler2.gibIP(), spieler2.gibPort(), "+PASSIVE");
            send(spieler1.gibIP(), spieler1.gibPort(), "-Spielzuege werden mit MOVE <x> <y> gesetzt.");
            send(spieler1.gibIP(), spieler1.gibPort(), "-Spielzuege werden mit MOVE <x> <y> gesetzt.");
            System.out.println("Spiel gestartet mit "+spieler1.gibName()+" und "+spieler2.gibName());
            registriereSpielInDB(spieler1, spieler2);
        }
    }

    private VierGewinntSpiel gibSpielNachSpieler(Spieler pSpieler) {
        spiele.toFirst();
        while (spiele.hasAccess()) {
            VierGewinntSpiel spiel = spiele.getContent();
            if (spiel.pruefeSpieler(pSpieler)) return spiel;
            spiele.next();
        }
        return null;
    }

    /**
     * Prueft auf erlaubte alphanumerische Zeichen im angeforderten Benutzernamen,
     * um die Datenbank vor Injektionen zu schuetzen.
     * @param pName angeforderter Benutzername
     * @return Wahrheitswert, ob der Name erlaubt ist
     */
    private boolean nameErlaubt(String pName) {
        boolean out = true;
        int i = 0;
        boolean kleinbuchstabe, grossbuchstabe, ziffer;
        while (out == true && i < pName.length()) {
            char[] c = pName.toCharArray();
            kleinbuchstabe = false;
            grossbuchstabe = false;
            ziffer = false;
            if (c[i] >=48 && c[i] <= 57) ziffer = true;
            if (c[i] >=65 && c[i] <= 90) grossbuchstabe = true;
            if (c[i] >=97 && c[i] <= 122) kleinbuchstabe = true;
            if (!ziffer && !grossbuchstabe && !kleinbuchstabe) out = false;
            i++;
        }
        return out;
    }

    private void registriereGewinnerInDB(Spieler pSpieler) {
    }

    private void registriereSpielInDB(Spieler pSpieler1, Spieler pSpieler2) {
    }
    /*private void beendeSpiel(VierGewinntSpiel pSpiel) 
    {

    }*/

    //Fuer die Verwendung auf Linux-Servern und Windows-Clients
    /*
    public void send(String pClientIP, int pClientPort, String pMessage){
    super.send(pClientIP,pClientPort,pMessage+"\r");
    }

    public void sendToAll(String pMessage) {
    super.sendToAll(pMessage+"\r");
    }
     */

    //     public void processWait(Spieler pClient, String pMessage) {
    //         String clientIP = pClient.gibIP();
    //         int clientPort = pClient.gibPort();
    //         String[] stuecke = pMessage.split(" ");
    //         if (stuecke.length == 2) {
    //             if (stuecke[0].equals("PLAY")){
    //                 // Ist ein Client mit dem gewuenschten Namen vorhanden?
    //                 Spieler gegenspieler = gibSpielerNachNamen(stuecke[1]);
    //                 if (gegenspieler != null){
    //                     // Zustände ändern
    //                     pClient.setzeZustand(PASSIVE);
    //                     gegenspieler.setzeZustand(PASSIVE);
    //                     send(clientIP, clientPort, "+STARTED " + stuecke[1]);
    //                     send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+STARTED " + pClient.gibName());
    //                 } else {
    //                     send(clientIP, clientPort, "-WAIT opponent not found");
    //                 }
    //             }
    // 
    //         } else if (pMessage.equals("QUIT")){
    //             send(clientIP, clientPort, "+OK see you soon");
    //             closeConnection(clientIP, clientPort);
    //             loescheClientNachIPUndPort(clientIP, clientPort);
    //             beendeSpiel();
    //         } else if (pMessage.equals("LISTPLAYERS")){
    //             send(clientIP, clientPort, "List of other players: ");
    //             spielerListe.toFirst();
    //             while (spielerListe.hasAccess()) {
    //                 Spieler spieler = spielerListe.getContent();
    //                 send(clientIP, clientPort, spieler.gibName());
    //                 spielerListe.next();
    //             }
    //         } else {
    //             send(clientIP, clientPort, "-ERR unknown command");
    //         } 
    //     }
}