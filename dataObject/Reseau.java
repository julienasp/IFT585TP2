/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import dataObject.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import protocole.UDPPacket;


public class Reseau  {
    /**************************************/
    /****** PUBLIC STATIC ATTRIBUTS *******/
    /**************************************/
    final static public int LSROUTING = 0;
    final static public int DVROUTING = 1;
    
    
    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private int typeDeRoutage;
    private Hashtable<String, Routeur> listeRouteurs = new Hashtable<String, Routeur>();
    private Hashtable<String, Arc> listeArcs = new Hashtable<String,Arc>();
    private Hashtable<String, Hote> listeHotes = new Hashtable<String,Hote>();
    
    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Reseau.class);

    
    /**************************************/
    /************ CONSTRUCTOR *************/
    /**************************************/
    public Reseau(int typeDeRoutage) {
        this.typeDeRoutage = typeDeRoutage;
    }

   
    /**************************************/
    /********* GETTER AND SETTER **********/
    /**************************************/
    public int getTypeDeRoutage() {
        return typeDeRoutage;
    }

    public void setTypeDeRoutage(int typeDeRoutage) {
        this.typeDeRoutage = typeDeRoutage;
    }

    public Hashtable<String, Routeur> getListeRouteurs() {
        return listeRouteurs;
    }

    public void setListeRouteurs(Hashtable<String, Routeur> listeRouteurs) {
        this.listeRouteurs = listeRouteurs;
    }

    public Hashtable<String, Arc> getListeArcs() {
        return listeArcs;
    }

    public void setListeArcs(Hashtable<String, Arc> listeArcs) {
        this.listeArcs = listeArcs;
    }

    public Hashtable<String, Hote> getListeHotes() {
        return listeHotes;
    }

    public void setListeHotes(Hashtable<String, Hote> listeHotes) {
        this.listeHotes = listeHotes;
    }
    
    
    /**************************************/
    /********   UTILITY METHODS  **********/
    /**************************************/    
    public void ajouterRouteur(Routeur unRouteur) {
       listeRouteurs.put(unRouteur.getNomRouteur(), unRouteur);
    }
    
    public void retirerRouteur(String nomRouteur) {
       listeRouteurs.remove(nomRouteur);
    }
     
    public void ajouterArc(Arc unArc) {
       listeArcs.put(unArc.getNomArc(), unArc);
    }
    
     public void retirerArc(String nomArc) {
       listeArcs.remove(nomArc);
    }
     
     public void ajouterHote(Hote unHote) {
       listeHotes.put(unHote.getNomHote(), unHote);
    }
    
     public void retirerHote(String nomHote) {
       listeHotes.remove(nomHote);
    }
    
     
    /**************************************/
    /*************   METHODS  *************/
    /**************************************/
    public void start() {		
        logger.info("Reseau: Le reseau démarre son tinitialisation.");
        try {


        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Fin de l'initialisation du reseau");                
        }
    }    
        
    
}
