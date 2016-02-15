/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.Logger;

public class Routeur implements Runnable {
    private String nomRouteur;
    private int port;
    private int typeRoutage;
    private Hashtable<String, Routeur> listeRouteurs = new Hashtable<String, Routeur>();
    private Hashtable<String, Arc> listeArcs = new Hashtable<String,Arc>();
    private Hashtable<String, Hote> listeHotes = new Hashtable<String,Hote>();
    private Hashtable<Integer,Routeur> tableRoutageLS = new Hashtable<Integer,Routeur>();
    private Hashtable<Integer,Hote> tableRoutageHote = new Hashtable<Integer,Hote>();

    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Routeur.class);
    public Routeur(String nomRouteur, int port) {
        this.nomRouteur = nomRouteur;
        this.port = port;
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

    public Hashtable<Integer, Routeur> getTableRoutageLS() {
        return tableRoutageLS;
    }

    public void setTableRoutageLS(Hashtable<Integer, Routeur> tableDeRoutageLS) {
        this.tableRoutageLS = tableDeRoutageLS;
    }

    public Hashtable<String, Routeur> getListeRouteurs() {
        return listeRouteurs;
    }

    public void setListeRouteurs(Hashtable<String, Routeur> listeRouteurs) {
        this.listeRouteurs = listeRouteurs;
    }

    
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTypeRoutage() {
        return typeRoutage;
    }

    public void setTypeRoutage(int typeRoutage) {
        this.typeRoutage = typeRoutage;
    }
    
    

    public String getNomRouteur() {
        return nomRouteur;
    }

    public void setNomRouteur(String nomRouteur) {
        this.nomRouteur = nomRouteur;
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
    
    public void ajouterRouteur(Routeur unRouteur) {
       listeRouteurs.put(unRouteur.getNomRouteur(), unRouteur);
    }
    
    public void retirerRouteur(String nomRouteur) {
       listeRouteurs.remove(nomRouteur);
    }
    
    public void ajouterRouteurTableRoutageLS(int portDestitation,Routeur fowardRouter) {
       tableRoutageLS.put(portDestitation, fowardRouter);
    }
    
    public void retirerRouteurTableRoutageLS(int portDestitation) {
       tableRoutageLS.remove(portDestitation);
    }
    
    public void ajouterHoteTableRoutage(int portDestitation,Hote unHote) {
       tableRoutageHote.put(portDestitation, unHote);
    }
    
    public void retirerHoteTableRoutage(int portDestitation) {
       tableRoutageHote.remove(portDestitation);
    }
    private int trouverCoutPour(String routeurA, String routeurB){
        logger.info("Routeur: trouverCoutPour(): ");
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurA) && value.getRouteurB().getNomRouteur().equals(routeurB) ) || ( value.getRouteurA().getNomRouteur().equals(routeurB) && value.getRouteurB().getNomRouteur().equals(routeurA) )){
                return value.getCout();                
            }
        }  
        return -1; // retourne -1 si l'arc n'existe pas
    }
    public ArrayList<Routeur> trouverVoisin(String routeurSource){
        logger.info("Routeur: trouverVoisin():Permet de trouver les voisins d'un routeur source: ");
        
        ArrayList<Routeur> routeurVoisin = new ArrayList<Routeur>();
        
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.add(value.getRouteurB());
            }
            if(( value.getRouteurB().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.add(value.getRouteurA());
            }
        }
        return routeurVoisin;
    }
    private void calculPourLs(){
    logger.info("Routeur: calculPourLs(): Début de l'algorithme pour trouver les chemins les plus courts pour le routeur source: " + this.getNomRouteur());
    
        
    }
     public void start() {		
        
        try {            
           if(typeRoutage == Reseau.LSROUTING){
               //Génération des meilleurs chemins avec LS
               logger.info("Routeur: Le routeur: " + this.getNomRouteur() + "a été démarré sur le port: " + this.getPort());
               logger.info("Routeur: Le routeur: " + this.getNomRouteur() + "utilise un routage de type LS (LINK-STATE)");
               
               calculPourLs();               
           }

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
