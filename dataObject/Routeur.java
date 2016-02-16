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
    private Hashtable<String, Routeur> N = new Hashtable<String, Routeur>();
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
    
    
    //Permet de trouver le cout d'un arc qui relie deux routeurs
    private int trouverCoutPour(String routeurA, String routeurB){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): trouve le côut pour l'arc qui relie " + routeurA + " et " + routeurB);
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurA) && value.getRouteurB().getNomRouteur().equals(routeurB) ) || ( value.getRouteurA().getNomRouteur().equals(routeurB) && value.getRouteurB().getNomRouteur().equals(routeurA) )){
                logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): Le cout pour l'arc qui relie " + routeurA + " et " + routeurB + " est de: " + value.getCout());
                return value.getCout();                
            }
        }
        logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): aucun arc trouvé entre " + routeurA + " et " + routeurB);
        return -1; // retourne -1 si l'arc n'existe pas
    }
    
    
    //Permet de trouver tous les voisins d'un routeur
    private Hashtable<String,Routeur> trouverVoisin(String routeurSource){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverVoisin():Permet de trouver les voisins d'un routeur source: ");
        
        Hashtable<String,Routeur> routeurVoisin = new Hashtable<String,Routeur>();
        
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurB().getNomRouteur(),value.getRouteurB());
            }
            if(( value.getRouteurB().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurA().getNomRouteur(),value.getRouteurA());
            }
        }
        return routeurVoisin;
    }
    
    
    //Permet de  trouver tous les voisisn d'un routeur qui ne sont pas dans la liste N.
    private Hashtable<String,Routeur> trouverVoisinNonN(String routeurSource){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverVoisinNonN(): Permet de trouver les voisins du routeur: " + routeurSource);
        
        Hashtable<String,Routeur> routeurVoisin = new Hashtable<String,Routeur>();
        
        for (Arc value : listeArcs.values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurSource) && N.containsKey(value.getRouteurB().getNomRouteur()) == false )){
                routeurVoisin.put(value.getRouteurB().getNomRouteur(),value.getRouteurB());
            }
            if(( value.getRouteurB().getNomRouteur().equals(routeurSource) && N.containsKey(value.getRouteurA().getNomRouteur()) == false )){
                routeurVoisin.put(value.getRouteurA().getNomRouteur(),value.getRouteurA());
            }
        }
        logger.info("Routeur-" + this.getNomRouteur() +": trouverVoisinNonN(): les voisins de sont: " + routeurVoisin.toString());
        return routeurVoisin;
    }
    
    
    //Permet de trouver le routeur qui a le côute D(w) le plus bas.
    private String trouverPlusPetitDW(Hashtable<String, Routeur> listeW){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): on trouve le w le avec le plus petit D(w)");
        int indice = 100000;
        String nom = "";        
        for (Routeur routeurCourant : listeW.values()) {
            if(routeurCourant.getIndiceCoutLS() <= indice && N.containsKey(routeurCourant.getNomRouteur()) == false  )
            {
                indice = routeurCourant.getIndiceCoutLS();
                nom = routeurCourant.getNomRouteur();
            }
        }
        logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): le plus petit est: " + nom);

        return nom;
    }
    
    
    //Permet de déduire la table de routage Ls suite au calcul LS.
    private void calculerTableRoutageLS(Hashtable<String, Routeur> cloneListe){
        logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): Suite à l'algorithme utilisé en calculPourLs(), nous déduisons la table de routage LS.");
        
        for (Routeur routeurCourant : cloneListe.values()) {            
             Routeur fowardRouteur = trouverFoward(cloneListe,routeurCourant);
             ajouterRouteTableRoutageLS(routeurCourant.getPort(),fowardRouteur);
             logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): pour se rendre à: " + routeurCourant.getNomRouteur() + " on foward vers: " + fowardRouteur.getNomRouteur());
        } 
        
    }
    
    //Fonction récursive qui nous permet de trouver le routeur à qui nous devons transferer
    private Routeur trouverFoward(Hashtable<String, Routeur> cloneListe, Routeur r){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverFoward(): Nous cherchons le prédécesseur de: " + r.getNomRouteur());

        if(r.getPredecesseurRouteurLS().equals(this.getNomRouteur()) ) return r;        
        else return trouverFoward(cloneListe,cloneListe.get(r.getPredecesseurRouteurLS()));
    }
    
    
    //Permet de trouver le chemin optimale pour l'instance du routeur.
    private void calculPourLs(){
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): Début de l'algorithme pour trouver les chemins les plus courts");
 
        //Ajout du chemin le plus court pour le routeur source
        ajouterRouteTableRoutageLS(this.getPort(),this);
        
        //Ajout du source dans N
        N.put(this.getNomRouteur(),this);
        
        //Routeur voisin de source
        Hashtable<String,Routeur> routeurVoisin = trouverVoisin(this.getNomRouteur());
        
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): initialisation des côuts D(v) pour le routeur: " + this.getNomRouteur());
        
        //On évite que tous les threads modifie la même liste.
        Hashtable<String, Routeur> cloneListe = new Hashtable<String, Routeur>(listeRouteurs) ;  
        
        for (Routeur routeurCourant : cloneListe.values()) {
            if(routeurVoisin.containsKey(routeurCourant.getNomRouteur())){
                routeurCourant.setIndiceCoutLS(trouverCoutPour(this.getNomRouteur(),routeurCourant.getNomRouteur()));
                routeurCourant.setPredecesseurRouteurLS(this.getNomRouteur());
            }
            else{
                routeurCourant.setIndiceCoutLS(1000000); //infini
            }
        } 
        
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): debut du do while"); 
        do{
            

            //On trouve le routeur avec la podération la plus petite
            String w = trouverPlusPetitDW(cloneListe);
            Routeur rW = cloneListe.get(w);
            
            //On ajoute le routeur dans notre liste de routeur ayant le chemin le plus optimale
            N.put(w, cloneListe.get(w)); // On ajoute le routeur courant à la liste N
            
            logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): N à été MaJ. le routeur: "+ rW.getNomRouteur() + " à comme prédécesseur: " + rW.getPredecesseurRouteurLS());
            
            //On récupere les voisins de w, qui ne sont pas déja dans N
            routeurVoisin = trouverVoisinNonN(w);
            
            //On récupere le routeur en lien avec w.
            
            for (Routeur routeurCourant : routeurVoisin.values()) {
                //On vérifie si l'indice de cout d(v) est plus petit que l'addition de d(w) + c(w,v)
                if(routeurCourant.getIndiceCoutLS() > ( rW.getIndiceCoutLS() + trouverCoutPour(w,routeurCourant.getNomRouteur()) ) ){
                    //l'indice du chemin via w, est inférieur alors on met à jour
                    routeurCourant.setIndiceCoutLS(rW.getIndiceCoutLS() + trouverCoutPour(w,routeurCourant.getNomRouteur()));
                    routeurCourant.setPredecesseurRouteurLS(w);
                }                
            } 
        logger.info("Routeur-" + this.getNomRouteur() +": N ressemble à: " + N.toString());    
        }while(N.size() != cloneListe.size());
        
    logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): calcul terminé.");
    logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): création de la table de routage avec les données obtenues.");
        
     ajouterRouteTableRoutageLS(this.getPort(),this);   
     calculerTableRoutageLS(cloneListe);   
        
    }
     public void start() {		
        
        try {            
           if(typeRoutage == Reseau.LSROUTING){
               //Génération des meilleurs chemins avec LS
               logger.info("Routeur-" + this.getNomRouteur()+ " a été démarré sur le port: " + this.getPort());
               logger.info("Routeur-" + this.getNomRouteur() + " utilise un routage de type LS (LINK-STATE)");
               
               calculPourLs();               
           }

        } catch (Exception e) {
                System.out.println("IO: " + e.getMessage());
        }
        finally {
                logger.info("Routeur-" + this.getNomRouteur() +" Fin du thread.");                
        }
    }  
     @Override
    public void run() {
            start();	
    }  
    
}
