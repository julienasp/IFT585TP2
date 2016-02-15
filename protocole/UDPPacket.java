package protocole;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPPacket implements Serializable {

    private static final long serialVersionUID = 2622663736791338175L;
		
    final static public int UPDATE = 0;
    final static public int FOWARD = 1;
		
    private int type;				// type de connexion upload ou download
    private int seq = 0; 			// numéro de séquence 
    private int ack = 0;                        // numéro d'acknowledgement
    private int fin = 0;                        // attribut pour savoir si le paquet udp était le dernier
    private byte[] data = null;       //Array avec les data de notre objets
    private InetAddress destination; 		// destinataire du message
    private int destinationPort;		// port du destinataire
    private InetAddress sourceAdr; 		// adresse de l'émetteur
    private int sourcePort;                     // port source du paquet
    private InetAddress destinationGatewayAdr;  // adresse de la passerelle destinataire du message
    private int destinationGatewayPort;         // port du routeur de l'hôte source
    
    
    

    /*************************************************************/
    /***BELOW WE CAN FIND ALL CONSTRUCTORS FOR AN UDPPACKET*******/
    /*************************************************************/
    
    
    public UDPPacket(int type, int destinationPort, int sourcePort) {
        this.type = type;
        this.destinationPort = destinationPort;
        this.sourcePort = sourcePort;        
        try {
            this.sourceAdr = InetAddress.getLocalHost();
            this.destination = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UDPPacket(int type, int destinationGatewayPort, int destinationPort, int sourcePort) {
        this.type = type;
        this.destinationPort = destinationPort;
        this.sourcePort = sourcePort;
        this.destinationGatewayPort = destinationGatewayPort;
        try {
            this.sourceAdr = InetAddress.getLocalHost();
            this.destination = InetAddress.getLocalHost();
            this.destinationGatewayAdr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UDPPacket(int type, InetAddress sourceAdr, int sourcePort, InetAddress destination, int destinationPort) {
        this.type = type;
        this.sourceAdr = sourceAdr;
        this.sourcePort = sourcePort;
        this.destination = destination;
        this.destinationPort = destinationPort;
    }

    
    /*************************************************************/
    /**BELOW WE CAN FIND ALL THE USEFUL METHODS FOR AN UDPPACKET**/
    /*************************************************************/
    
    public boolean isForUpdate(){
        return (type == UPDATE);
    }
    
    public boolean isForFoward(){
        return (type == FOWARD);
    }
    
    public boolean isTheLastPacket(){
        return (fin == 1);
    }

    public InetAddress getSourceAdr() {
        return sourceAdr;
    }

    public void setSourceAdr(InetAddress sourceAdr) {
        this.sourceAdr = sourceAdr;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public InetAddress getDestinationGatewayAdr() {
        return destinationGatewayAdr;
    }

    public void setDestinationGatewayAdr(InetAddress destinationGatewayAdr) {
        this.destinationGatewayAdr = destinationGatewayAdr;
    }

    public int getDestinationGatewayPort() {
        return destinationGatewayPort;
    }

    public void setDestinationGatewayPort(int destinationGatewayPort) {
        this.destinationGatewayPort = destinationGatewayPort;
    }
    
    

 
    /**************************************************/
    /**BELOW YOU WILL FIND EVERYONE SETTER AND GETTER**/
    /**************************************************/
    
    
    
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
            this.type = type;
    }
    public int getSeq() {
            return seq;
    }
    public void setSeq(int seq) {
            this.seq = seq;
    }

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }
    
    
    public InetAddress getDestination() {
            return destination;
    }
    public int getDestinationPort() {
            return destinationPort;
    }
    public void setDestinationPort(int destinationPort) {
            this.destinationPort = destinationPort;
    }
    public void setDestination(InetAddress destination) {
            this.destination = destination;
    }
    
    @Override
    public String toString() {
            String output = "UDPPacket [type=" + type + ", seq=" + this.getSeq() + ", ack=" + this.getAck() + ", fin=" + this.getFin()
                            + ", source=" + sourceAdr + ", sourcePort=" + sourcePort+ ", destination=" + destination + ", destinationPort=" + destinationPort; 
            return output;	
    }
}
