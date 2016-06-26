/**
 * Klasse GameServer.
 * 
 * @author Heiko Dudzus
 * @version 2016-04-27
 */

public class GameServer extends Server implements Zustand
{
    //private DBVierGewinnt db;
    private List<Spieler> spielerListe;
    private VierGewinntSpiel spiel;
    
    /**
     * Konstruktor fuer Objekte der Klasse GameServer
     */
    public GameServer(int port)
    {
        super(port);
        spielerListe = new List<Spieler>();
        System.out.println("Der Server lauscht an Port "+port+"!");
    }

    public void processNewConnection(String pClientIP, int pClientPort) {
        send(pClientIP, pClientPort, "Herzlich Willkommen auf dem Vier-Gewinnt-Gameserver!");
        send(pClientIP, pClientPort, "\"QUIT\" beendet die Verbindung!");
        System.out.println(pClientIP + " : " + pClientPort + " hat sich eingewaehlt.");
        Spieler spieler = new Spieler(pClientIP, pClientPort);
        spielerListe.append(spieler);
        send(pClientIP, pClientPort, "Waehlen Sie einen Nickname mit NICK <name>,");
        sendToAll(pClientIP + " : " + pClientPort + " betritt den Chat.");
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
                case ACTIVE :
                case LOST :
                case WON :
                default: System.out.println("Fehler, Client-Zustand nicht definiert.");
            }
        } else {
            System.out.println("PANIC - kein Client mit diesen Daten.");
        }
    }

    public void processNickname(Spieler pClient, String pMessage){
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        if (stuecke.length == 2) {
            // Ist stuecke[0] gleich NICK?
            if (stuecke[0].equals("NICK")){
                // Ist ein Client mit dem gew�nschten Namen noch nicht vorhanden?
                if (!nameVorhanden(stuecke[1])){
                    pClient.setzeName(stuecke[1]);
                    pClient.setzeZustand(WAIT);
                    send(clientIP, clientPort, "+OK " + stuecke[1]);
                    sendToAll(stuecke[1] + " logged on to the server");
                } else {
                    send(clientIP, clientPort, "-INVALID name invalid");
                }
            }

        } else if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "+OK see you soon");
            closeConnection(clientIP, clientPort);
            loescheClientNachIPUndPort(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "-ERR unknown command");
        } 
    }

    public void processWait(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        String[] stuecke = pMessage.split(" ");
        if (stuecke.length == 2) {
            if (stuecke[0].equals("PLAY")){
                // Ist ein Client mit dem gewuenschten Namen vorhanden?
                Spieler gegenspieler = gibSpielerNachNamen(stuecke[1]);
                if (gegenspieler != null){
                    // Zustände ändern
                    pClient.setzeZustand(PASSIVE);
                    gegenspieler.setzeZustand(PASSIVE);
                    send(clientIP, clientPort, "+STARTED " + stuecke[1]);
                    send(gegenspieler.gibIP(), gegenspieler.gibPort(), "+STARTED " + pClient.gibName());
                } else {
                    send(clientIP, clientPort, "-WAIT opponent not found");
                }
            }

        } else if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "+OK see you soon");
            closeConnection(clientIP, clientPort);
            loescheClientNachIPUndPort(clientIP, clientPort);
        } else if (pMessage.equals("LISTPLAYERS")){
            send(clientIP, clientPort, "List of other players: ");
            spielerListe.toFirst();
            while (spielerListe.hasAccess()) {
                Spieler spieler = spielerListe.getContent();
                send(clientIP, clientPort, spieler.gibName());
                spielerListe.next();
            }
        } else {
            send(clientIP, clientPort, "-ERR unknown command");
        } 
    }

    public void processPassive(Spieler pClient, String pMessage) {
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();
        send(clientIP, clientPort, "-ERR it is not your turn");
        // Wie erfährt der Gameserver vom assoziierten Spiel, wenn der Spieler an der Reihe ist?
        // direkter Zugriff auf Spieler?
    }
    
    public void loescheClientNachIPUndPort(String pClientIP, int pClientPort){
        spielerListe.toFirst();
        while (spielerListe.hasAccess()) {
            Spieler spieler = spielerListe.getContent();
            if (spieler.vergleicheIPUndPort(spieler.gibIP(), spieler.gibPort())) {
                spielerListe.remove();
            }
            spielerListe.next();
        }
    }

    public Spieler sucheClientNachIPUndPort(String pClientIP, int pClientPort){
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

    public void processChat(Spieler pClient, String pMessage){
        String clientIP = pClient.gibIP();
        int clientPort = pClient.gibPort();

        if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "Auf Wiedersehen");
            closeConnection(clientIP, clientPort);
            loescheClientNachIPUndPort(clientIP, clientPort);
        } else {
            sendToAll(pClient.gibName() + " > " + pMessage);
        } 
    }

    public boolean nameVorhanden(String pName) {
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
    }

    //Fuer die Verwendung auf Linux-Servern und Windows-Clients
    /*
    public void send(String pClientIP, int pClientPort, String pMessage){
    super.send(pClientIP,pClientPort,pMessage+"\r");
    }

    public void sendToAll(String pMessage) {
    super.sendToAll(pMessage+"\r");
    }
     */
}