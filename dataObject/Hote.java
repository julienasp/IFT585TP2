/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import utils.Marshallizer;

public class Hote implements Runnable{
    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private String nomHote;
    private int port;
    private int passerellePort;
    private DatagramSocket hoteSocket = null;
    private DatagramPacket packetReceive;
    
    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Hote.class);

    
    /**************************************/
    /************ CONSTRUCTOR *************/
    /**************************************/
    public Hote(String nomHote, int port, int passerellePort) {
        this.nomHote = nomHote;
        this.port = port;
        this.passerellePort = passerellePort;
    }
    
    
    /**************************************/
    /********* GETTER AND SETTER **********/
    /**************************************/
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
    
    
    /**************************************/
    /********   UTILITY METHODS  **********/
    /**************************************/ 
    private UDPPacket buildPacket(int destinationPort,byte[] data) {
         logger.info("Hote-" + nomHote + ":  creation du paquet pour le message.");
         UDPPacket packet = new UDPPacket(UDPPacket.FOWARD,destinationPort, port);         
         packet.setData(data);                
         logger.debug(packet.toString());
         return packet;
    }
    
    private void sendPacket(UDPPacket udpPacket) {
        try {
                logger.info("Hote-" + nomHote + ": sendPacket executed");
                logger.info("Hote-" + nomHote + ": sendPacket : " + udpPacket.toString());                
                byte[] packetData = Marshallizer.marshallize(udpPacket);
                DatagramPacket datagram = new DatagramPacket(packetData,
                                packetData.length, 
                                udpPacket.getDestination(),
                                passerellePort); // port de la passerelle par default
                hoteSocket.send(datagram); // émission non-bloquante
        } catch (SocketException e) {
                System.out.println("Hote-" + nomHote +" Socket: " + e.getMessage());
        } catch (IOException e) {
                System.out.println("Hote-" + nomHote +" IO: " + e.getMessage());
        }
    }
    
    
    /**************************************/
    /*************   METHODS  *************/
    /**************************************/
    public void envoyerMessage(String message, int destinationPort){
        logger.info("Hote-" + nomHote + ": envoyerMessage executed");        
        UDPPacket monMessage = buildPacket(destinationPort,message.getBytes());
        sendPacket(monMessage);
    }
    
    public void start() {		
        try {
                hoteSocket = new DatagramSocket(port); // port pour l'envoi et l'écoute
                
                boolean run = true;
                byte[] buffer = new byte[1500];
                
                packetReceive = new DatagramPacket(buffer, buffer.length);               
                
              do  {                   
                    
                    logger.info("Hote-" + nomHote + ": waiting for a packet");
                    hoteSocket.receive(packetReceive); // reception bloquante
                    logger.info("Hote-" + nomHote + ": a packet was receive");                   
                    
                    UDPPacket packet = (UDPPacket) Marshallizer.unmarshall(packetReceive);
                    String monString = new String(packet.getData());
                    logger.info("Hote-" + nomHote + " le packet reçu contient les informations suivantes: " + monString.toString());   
     					
                }while (run);
        } catch (SocketException e) {
                System.out.println("Hote-" + nomHote +" Socket: " + e.getMessage());
        } catch (IOException e) {
                System.out.println("Hote-" + nomHote +" IO: " + e.getMessage());
        }
        finally {
                logger.info("Hote-" + nomHote + ": end of transmission");
                stop();
        }
    }
    
    public void stop(){
        hoteSocket.close();
        Thread.currentThread().interrupt();        
        logger.info("Hote-" + nomHote + ": stop() executed");
    }
    
    @Override
    public void run() {
            start();	
    }  
}
    
    
    
  

