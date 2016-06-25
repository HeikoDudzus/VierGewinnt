/**
 * Klasse GameServer.
 * 
 * @author Heiko Dudzus
 * @version 2016-04-27
 */

public class GameServer extends Server
{
    //private DBVierGewinnt db;
    private List<Spieler> spielerListe;
    private XxoSpiel spiel;
    private static final int NICKNAME = 0;
    private static final int WAIT = 1;
    private static final int PASSIVE = 2;
    private static final int ACTIVE = 3;
    private static final int LOST = 4;
    private static final int WON = 5;

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
        send(pClientIP, pClientPort, "Herzlich Willkommen beim Chat!");
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
                case WAIT : processChat(spieler, pMessage); break;
                case PASSIVE :
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
                    pClient.setzeZustand(2);
                    send(clientIP, clientPort, "Ihr Name ist " + stuecke[1]);
                    sendToAll(stuecke[1] + " hat den Chat betreten.");
                } else {
                    send(clientIP, clientPort, "Der Name " + stuecke[1] + " ist schon vergeben.");
                }
            }

        } else if (pMessage.equals("QUIT")){
            send(clientIP, clientPort, "Auf Wiedersehen");
            closeConnection(clientIP, clientPort);
            loescheClientNachIPUndPort(clientIP, clientPort);
        } else {
            send(clientIP, clientPort, "Unbekannter Befehl");
        } 
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