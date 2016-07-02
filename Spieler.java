
/**
 * Write a description of class Client here.
 * 
 * @author Heiko Dudzus (Die Hinterbänkler)
 * @version 2016-06-24
 */

public class Spieler implements Zustand
{
    private String name;
    private String ip;
    private int port;
    private int zustand;
    private String symbol;

    /**
     * Constructor for objects of class Client
     */
    public Spieler()
    {
        
    }
    
    public Spieler(String pIP,int pPort) {
        name = null;
        this.ip = pIP;
        this.port = pPort;
        zustand = NICKNAME;
    }
    
    public String gibName()
    {
        return name;
    }

    public String gibIP(){
        return ip;
    }

    public int gibPort(){
        return port;
    }

    public int gibZustand(){
        return zustand;
    }
    
    public String gibSymbol() {
        return symbol;
    }
    
    public void setzeSymbol(String pSymbol) {
        symbol = pSymbol;
    }
    
    public void setzeName(String pName){
        this.name = pName;
    }

    public void setzeZustand(int pZustand){
        this.zustand = pZustand;
    }

    public boolean vergleicheName(String pName){
        if (this.name == null) {
            return false;
        } else {
            return this.name.equals(pName);
        }
    }

    public boolean vergleicheIPUndPort(String pIP, int pPort) {
        return (this.ip.equals(pIP) && this.port == pPort);
    }
}
