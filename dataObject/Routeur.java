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
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import utils.Marshallizer;

public class Routeur implements Runnable {
    
    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/    
    private String nomRouteur;
    private DatagramSocket routeurSocket = null;
    private DatagramPacket packetReceive;
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
    private Hashtable<Integer,Routeur> tableRoutageDV = new Hashtable<Integer,Routeur>();
    private Hashtable <String,Integer> coutRoutageDV = new Hashtable<String,Integer>();
    private Hashtable <Integer,Routeur> receivedTable;

    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(Routeur.class);
    
    
    /**************************************/
    /************ CONSTRUCTORS *************/
    /**************************************/
    public Routeur(Routeur r) {
        this.nomRouteur = r.getNomRouteur();
        this.port = r.getPort();
        this.typeRoutage = r.getTypeRoutage();
        this.indiceCoutLS = r.getIndiceCoutLS();
        this.predecesseurRouteurLS = r.getPredecesseurRouteurLS();
    }
    
    public Routeur(String nomRouteur, int port) {
        this.nomRouteur = nomRouteur;
        this.port = port;
    }

    
    /**************************************/
    /********* GETTER AND SETTER **********/
    /**************************************/
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

    public DatagramSocket getRouteurSocket() {
        return routeurSocket;
    }

    public void setRouteurSocket(DatagramSocket routeurSocket) {
        this.routeurSocket = routeurSocket;
    }

    public DatagramPacket getPacketReceive() {
        return packetReceive;
    }

    public void setPacketReceive(DatagramPacket packetReceive) {
        this.packetReceive = packetReceive;
    }

    public Hashtable<String, Routeur> getN() {
        return N;
    }

    public void setN(Hashtable<String, Routeur> N) {
        this.N = N;
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

    public Hashtable<Integer, Routeur> getTableRoutageDV() {
        return tableRoutageDV;
    }

    public void setTableRoutageDV(Hashtable<Integer, Routeur> tableRoutageDV) {
        this.tableRoutageDV = tableRoutageDV;
    }

    public Hashtable<String, Integer> getCoutRouteurDV() {
        return coutRoutageDV;
    }

    public void setCoutRouteurDV(Hashtable<String, Integer> coutRouteurDV) {
        this.coutRoutageDV = coutRouteurDV;
    }   

    public Hashtable<Integer, Routeur> getReceivedTable() {
        return receivedTable;
    }

    public void setReceivedTable(Hashtable<Integer, Routeur> receivedTable) {
        this.receivedTable = receivedTable;
    } 
    
    
    /**************************************/
    /********   UTILITY METHODS  **********/
    /**************************************/
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
    
    public void ajouterRouteTableRoutageDV(int portDestitation,Routeur fowardRouter) {
       tableRoutageDV.put(portDestitation, fowardRouter);
    }
    
    public void retirerRouteTableRoutageDV(int portDestitation) {
       tableRoutageDV.remove(portDestitation);
    }
    
    public synchronized void ajouterCoutRoutageDV(String fowardRouter,int cout) {
       coutRoutageDV.put(fowardRouter, cout);
    }
    
    public synchronized void retirerCoutRoutageDV(Routeur router) {
       coutRoutageDV.remove(router);
    }
    
    public void ajouterHoteTableRoutage(int portDestitation,Hote unHote) {
       tableRoutageHote.put(portDestitation, unHote);
    }
    
    public synchronized void retirerHoteTableRoutage(int portDestitation) {
       tableRoutageHote.remove(portDestitation);
    }
    
    
    /**************************************/
    /*************   METHODS  *************/
    /**************************************/
    //Permet de trouver le cout d'un arc qui relie deux routeurs
    private synchronized int trouverCoutPour(String routeurA, String routeurB){
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
    private synchronized Hashtable<String,Routeur> trouverVoisin(String routeurSource){
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
    private synchronized Hashtable<String,Routeur> trouverVoisinNonN(String routeurSource){
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
    private synchronized String trouverPlusPetitDW(Hashtable<String, Routeur> listeW){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): on trouve le w le avec le plus petit D(w)");
        int indice = 100000;
        String nom = "";
        //logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): intérieur:" + listeW.toString());

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
    private synchronized void calculerTableRoutageLS(Hashtable<String, Routeur> cloneListe){
        logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): Suite à l'algorithme utilisé en calculPourLs(), nous déduisons la table de routage LS.");
        
        for (Routeur routeurCourant : cloneListe.values()) {
            if(!routeurCourant.getNomRouteur().equals(this.getNomRouteur())){ 
                logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): Début calcul pour la destination: " + routeurCourant.getNomRouteur());

                Routeur fowardRouteur = trouverFoward(cloneListe,routeurCourant);
                ajouterRouteTableRoutageLS(routeurCourant.getPort(),fowardRouteur);
                logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): pour se rendre à: " + routeurCourant.getNomRouteur() + " on foward vers: " + fowardRouteur.getNomRouteur());
            }
        } 
        
        logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): la table de routage a été généré.");
        logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): table de routage: " + this.getTableRoutageLS().toString());
    }
    
    
    //Fonction récursive qui nous permet de trouver le routeur à qui nous devons transferer
    private synchronized Routeur trouverFoward(Hashtable<String, Routeur> cloneListe, Routeur r){
        logger.info("Routeur-" + this.getNomRouteur() +": trouverFoward(): Nous cherchons le prédécesseur de: " + r.getNomRouteur());

        if(r.getPredecesseurRouteurLS().equals(this.getNomRouteur()) ) return r;        
        else return trouverFoward(cloneListe,cloneListe.get(r.getPredecesseurRouteurLS()));
    }
    
    
    //Permet de trouver le chemin optimale pour l'instance du routeur.
    private synchronized void calculPourLs(){
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): Début de l'algorithme pour trouver les chemins les plus courts");
 
        //Ajout du chemin le plus court pour le routeur source
        ajouterRouteTableRoutageLS(this.getPort(),this);
        
        //Ajout du source dans N
        N.put(this.getNomRouteur(),this);
        
        //Routeur voisin de source
        Hashtable<String,Routeur> routeurVoisin = trouverVoisin(this.getNomRouteur());
        
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): initialisation des côuts D(v) pour le routeur: " + this.getNomRouteur());
        
        //On évite que tous les threads modifie la même liste.
        Hashtable<String, Routeur> cloneListe = new Hashtable<String, Routeur>();
        for (Routeur routeurCourant : listeRouteurs.values()) {            
                cloneListe.put(routeurCourant.getNomRouteur(), new Routeur(routeurCourant));
        }
        
        //STEP 1, on met tous à infini et on met les couts pour nos voisins
        for (Routeur routeurCourant : cloneListe.values()) {
            if(routeurVoisin.containsKey(routeurCourant.getNomRouteur())){
                routeurCourant.setIndiceCoutLS(trouverCoutPour(this.getNomRouteur(),routeurCourant.getNomRouteur()));
                routeurCourant.setPredecesseurRouteurLS(this.getNomRouteur());
            }
            else{
                routeurCourant.setIndiceCoutLS(1000000); //infini
            }
        } 
        
        //STEP 2,
        logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): debut du do while"); 
        do{
            

            //On trouve le routeur avec la podération la plus petite
            String w = trouverPlusPetitDW(cloneListe);
            Routeur rW = cloneListe.get(w);

            //On ajoute le routeur dans notre liste de routeur ayant le chemin le plus optimale
            N.put(w, rW); // On ajoute le routeur courant à la liste N
            
            logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): N à été MaJ. le routeur: "+ rW.getNomRouteur() + " à comme prédécesseur: " + rW.getPredecesseurRouteurLS());
            
            //On récupere les voisins de w, qui ne sont pas déja dans N
            routeurVoisin = trouverVoisinNonN(w);
            
            //Corrige les pointeurs
            for (Routeur r : routeurVoisin.values()) {
                routeurVoisin.replace(r.getNomRouteur(), cloneListe.get(r.getNomRouteur()));
            } 
            
            logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): recupere les voisins de w");
            
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

        //STEP 3
        calculerTableRoutageLS(cloneListe); 
    }
    
    
    //Permet de foward un paquet vers la destination reçu en param
    private void sendPacket(UDPPacket udpPacket, int destinationPort) {
        try {
                logger.info("Routeur-" + this.getNomRouteur() + ": sendPacket executed");
                logger.info("Routeur-" + this.getNomRouteur() + ": sendPacket : " + udpPacket.toString());                
                byte[] packetData = Marshallizer.marshallize(udpPacket);
                DatagramPacket datagram = new DatagramPacket(packetData,
                                packetData.length, 
                                udpPacket.getDestination(),
                                destinationPort); // port de la passerelle par default
                routeurSocket.send(datagram); // émission non-bloquante
        } catch (SocketException e) {
                System.out.println("Routeur-" + this.getNomRouteur() + " Socket: " + e.getMessage());
        } catch (IOException e) {
                System.out.println("Routeur-" + this.getNomRouteur() + " IO: " + e.getMessage());
        }
    }
    
    
    /**************************************/
    /*************   THREAD   *************/
    /**************************************/
    public void start() {		
        
        try {
            logger.info("Routeur-" + this.getNomRouteur()+ " a été démarré sur le port: " + this.getPort());
            routeurSocket = new DatagramSocket(this.getPort()); // port pour l'envoi et l'écoute                
            
            byte[] buffer = new byte[1500];
                
            packetReceive = new DatagramPacket(buffer, buffer.length); 
            
           if(typeRoutage == Reseau.LSROUTING){
               //Génération des meilleurs chemins avec LS               
               logger.info("Routeur-" + this.getNomRouteur() + " utilise un routage de type LS (LINK-STATE)");
               
               calculPourLs();               
           }
           if(typeRoutage == Reseau.DVROUTING){
               //Initiation des tables de routage pour DV
                logger.info("Routeur-" + this.getNomRouteur() + " utilise un routage de type DV (DISTANCE VECTOR)");
                Thread DVThread = new Thread(new DVHandler(this));
                DVThread.start();
                logger.info("Routeur-" + this.getNomRouteur() + ": Update Thread Started.");                       
           }             
            
           //Début de la boucle pour recevoir des paquets
            do  {                   
                    
                    logger.info("Routeur-" + this.getNomRouteur() + ": waiting for a packet");
                    routeurSocket.receive(packetReceive); // reception bloquante
                    logger.info("Routeur-" + this.getNomRouteur() + ": a packet was receive");                   
                    
                    UDPPacket packet = (UDPPacket) Marshallizer.unmarshall(packetReceive);
                    
                    //Paquet de type FOWARD
                    if(packet.isForFoward()){
                        
                        logger.info("Routeur-" + this.getNomRouteur() + ": a reçu un paquet de type foward");
                        logger.info("Routeur-" + this.getNomRouteur() + ": on regarde si la destination est dans notre table d'hôte");
                        
                        if(tableRoutageHote.containsKey(packet.getDestinationPort())){
                            logger.info("Routeur-" + this.getNomRouteur() + ": la destination est dans notre table d'hôte");
                            
                            sendPacket(packet,packet.getDestinationPort());
                            logger.info("Routeur-" + this.getNomRouteur() + ": le paquet à été remis à l'hôte: " + tableRoutageHote.get(packet.getDestinationPort()));

                        }
                        else
                        {
                            logger.info("Routeur-" + this.getNomRouteur() + ": la destination n'est pas dans notre table d'hôte. Alors on FOWARD.");                           
                            logger.info("Routeur-" + this.getNomRouteur() + ": PORT DESTINATION GATEWAY: " + packet.getDestinationGatewayPort());                            
                            int portDestination = (typeRoutage == Reseau.LSROUTING) ?  tableRoutageLS.get(packet.getDestinationGatewayPort()).getPort() : tableRoutageDV.get(packet.getDestinationGatewayPort()).getPort();
                            String destinataire = (typeRoutage == Reseau.LSROUTING) ?  tableRoutageLS.get(packet.getDestinationGatewayPort()).getNomRouteur() : tableRoutageDV.get(packet.getDestinationGatewayPort()).getNomRouteur();
               
                            sendPacket(packet,portDestination);
                            logger.info("Routeur-" + this.getNomRouteur() + ": le paquet à été transmis à: " + destinataire);
                            
                        }                        
                        
                        logger.info("Routeur-" + this.getNomRouteur() + " le packet reçu à été bien été transmis.");   
                    }
                    //Paquet de type UPDATE pour un DVHandler
                    if(packet.isForUpdate()){
                        logger.info("Routeur-" + this.getNomRouteur() + ": a reçu un paquet de type update");
                        //Commencer un thread de DVHandler
                        Thread DVThread = new Thread(new DVHandler(packet,this));                      
                        DVThread.start();
			logger.info("Routeur-" + this.getNomRouteur() + ": Update Thread Started.");
                    }
            }while (true);

        }catch (SocketException e) {
                System.out.println("SocketException-Routeur-" + this.getNomRouteur() + " " + e.getMessage());
        }catch (IOException e) {
                System.out.println("IOException-Routeur-" + this.getNomRouteur() + " " + e.getMessage());
        }catch (Exception e) {
                System.out.println("Exception-Routeur-" + this.getNomRouteur() + " " + e.getMessage());
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
