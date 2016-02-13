/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;
public class Hote {
    private String nomHote;
    private int port;
    private int passerellePort;

    public Hote(String nomHote, int port, int passerellePort) {
        this.nomHote = nomHote;
        this.port = port;
        this.passerellePort = passerellePort;
    }
    
    

    public String getNomHote() {
        return nomHote;
    }

    public void setNomHote(String nomHote) {
        this.nomHote = nomHote;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPasserellePort() {
        return passerellePort;
    }

    public void setPasserellePort(int passerellePort) {
        this.passerellePort = passerellePort;
    }
    
    public void envoyerMessage(String message, int Destination){
    
    }
}
