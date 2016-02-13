/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import org.apache.log4j.Logger;

public class Routeur implements Runnable {
    private String nomRouteur;
    private int port;

       //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Routeur.class);
    public Routeur(String nomRouteur, int port) {
        this.nomRouteur = nomRouteur;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    

    public String getNomRouteur() {
        return nomRouteur;
    }

    public void setNomRouteur(String nomRouteur) {
        this.nomRouteur = nomRouteur;
    }
    
     public void start() {		
        
        try {            
           

        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Fin de l'initialisation du reseau");                
        }
    }  
     @Override
    public void run() {
            start();	
    }  
    
}
