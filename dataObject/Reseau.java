/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import server.UDPPacketHandler;
import server.UDPServer;

public class Reseau implements Runnable {
    /**************************************/
    /****** PUBLIC STATIC ATTRIBUTS *******/
    /**************************************/
    final static public int LSROUTING = 0;
    final static public int DVROUTING = 1;
    
    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private static final Logger logger = Logger.getLogger(UDPServer.class);
    private Hashtable<String, Routeur> listeRouteurs = new Hashtable<String, Routeur>();
    private Hashtable<String, Arc> listeArcs = new Hashtable<String,Arc>();
    private Hashtable<String, Hote> listeHotes = new Hashtable<String,Hote>();
       
    public void start() {		
        logger.info("Reseau: Le reseau démarre.");
        try {


        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Fin du reseau");
                stop();
        }
    }    
        
    public void stop() {		
        try {
                Thread.currentThread().interrupt();
        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }		
    }
    @Override
    public void run() {
            start();	
    }
}
