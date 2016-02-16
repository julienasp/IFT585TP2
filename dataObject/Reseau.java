/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

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
    private ExecutorService pool;
    private Hashtable<String, Routeur> listeRouteurs = new Hashtable<String, Routeur>();
    private Hashtable<String, Arc> listeArcs = new Hashtable<String,Arc>();
    private Hashtable<String, Hote> listeHotes = new Hashtable<String,Hote>();
    
    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Reseau.class);

    
    /**************************************/
    /************ CONSTRUCTOR *************/
    /**************************************/
    public Reseau() {        
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
        logger.info("Reseau: Le reseau démarre sont initialisation.");
        try {
            //On creer un pool de thread afin de simplifier la fermeture
            pool = Executors.newFixedThreadPool(1);
            
            //Création des threads pour tous les routeurs
            logger.info("Reseau: ouverture des threads pour les routeurs");
            for (Routeur routeur : listeRouteurs.values()) {
                routeur.setTypeRoutage(typeDeRoutage); //On ajoute le type de routage
                routeur.setListeArcs(listeArcs); // On ajoute la topologie et les couts
                routeur.setListeRouteurs(listeRouteurs); // On ajoute les routeurs (pour LS)
                pool.execute(routeur); // On execute le thread
                logger.info("Reseau: new runnable pour le routeur: " + routeur.getNomRouteur());
            } 

        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Fin de l'initialisation du reseau");                
        }
    }
    
    public void stop(){
        // Disable new tasks from being submitted
        pool.shutdown(); 
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)){
                        logger.debug("Reseau: Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
        }
	
    }        
    
}
