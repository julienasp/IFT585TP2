/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import java.util.Hashtable;
import org.apache.log4j.Logger;

public class Routeur implements Runnable {
    private String nomRouteur;
    private int port;
    private int typeRoutage;
    private int indiceCoutLS;
    private String predecesseurRouteurLS;
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

    public int getIndiceCoutLS() {
        return indiceCoutLS;
    }

    public void setIndiceCoutLS(int indiceCoutLS) {
        this.indiceCoutLS = indiceCoutLS;
    }

    public Hashtable<Integer, Hote> getTableRoutageHote() {
        return tableRoutageHote;
    }

    public void setTableRoutageHote(Hashtable<Integer, Hote> tableRoutageHote) {
        this.tableRoutageHote = tableRoutageHote;
    }

    public String getPredecesseurRouteurLS() {
        return predecesseurRouteurLS;
    }

    public void setPredecesseurRouteurLS(String predecesseurRouteurLS) {
        this.predecesseurRouteurLS = predecesseurRouteurLS;
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
    
    public void ajouterRouteTableRoutageLS(int portDestitation,Routeur fowardRouter) {
       tableRoutageLS.put(portDestitation, fowardRouter);
    }
    
    public void retirerRouteTableRoutageLS(int portDestitation) {
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
    private Hashtable<String,Routeur> trouverVoisin(String routeurSource){
        logger.info("Routeur: trouverVoisin():Permet de trouver les voisins d'un routeur source: ");
        
        Hashtable<String,Routeur> routeurVoisin = new Hashtable<String,Routeur>();
        
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurB().getNomRouteur(),value.getRouteurB());
            }
            if(( value.getRouteurB().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurB().getNomRouteur(),value.getRouteurA());
            }
        }
        return routeurVoisin;
    }
    
    private String trouverPlusPetitDW(Hashtable<String, Routeur> listeW){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): on trouve le w le avec le plus petit D(w)");
        int indice = 100000;
        String nom = "";        
        for (Routeur routeurCourant : listeW.values()) {
            if(routeurCourant.getIndiceCoutLS() <= indice)
            {
                indice = routeurCourant.getIndiceCoutLS();
                nom = routeurCourant.getNomRouteur();
            }
        }        
        return nom;
    }
    private void calculPourLs(){
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): Début de l'algorithme pour trouver les chemins les plus courts pour le routeur source: " + this.getNomRouteur());

         //Sous-ensemble de routeur ou le chemin le plus court est connu
        Hashtable<String, Routeur> N = new Hashtable<String, Routeur>();
        
        //Ajout du chemin le plus court pour le routeur source
        ajouterRouteTableRoutageLS(this.getPort(),this);
        
        //Ajout du source dans N
        N.put(this.getNomRouteur(),this);
        
        //Routeur voisin de source
        Hashtable<String,Routeur> routeurVoisin = trouverVoisin(this.getNomRouteur());
        
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): initialisation des côuts D(v) pour le routeur: " + this.getNomRouteur());
        
        //On évite que tous les threads modifie la même liste.
        Hashtable<String, Routeur> cloneListe = new Hashtable<String, Routeur>(listeRouteurs) ;
        
        cloneListe.remove(this.getNomRouteur());
        
        for (Routeur routeurCourant : cloneListe.values()) {
            if(routeurVoisin.containsKey(routeurCourant.getNomRouteur())){
                routeurCourant.setIndiceCoutLS(trouverCoutPour(this.getNomRouteur(),routeurCourant.getNomRouteur()));
                routeurCourant.setPredecesseurRouteurLS(this.getNomRouteur());
            }
            else{
                routeurCourant.setIndiceCoutLS(1000000); //infini
            }
        } 
        
        do{
            String w = trouverPlusPetitDW(cloneListe);
        }while(true);
        
        
        
        
        
        
    }
     public void start() {		
        
        try {            
           if(typeRoutage == Reseau.LSROUTING){
               //Génération des meilleurs chemins avec LS
               logger.info("Routeur-" + this.getNomRouteur()+ "a été démarré sur le port: " + this.getPort());
               logger.info("Routeur-" + this.getNomRouteur() + "utilise un routage de type LS (LINK-STATE)");
               
               calculPourLs();               
           }

        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Routeur-" + this.getNomRouteur() +"Fin du thread.");                
        }
    }  
     @Override
    public void run() {
            start();	
    }  
    
}
