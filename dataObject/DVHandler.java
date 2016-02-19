/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

/**************************************/
/*************   IMPORTS  *************/
/**************************************/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import utils.Marshallizer;


public class DVHandler implements Runnable {

    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private Routeur myRouteur;
    private Hashtable<String,Routeur> routeurVoisin;
    private UDPPacket packetRecu;    
    private Hashtable <Integer,Routeur> neighborTable;	
    private Hashtable <String,Integer> coutRoutageDV;
    private Hashtable <Integer,Routeur> tableRoutageDV ;
    private boolean tableRoutageDVWasEdited =  false;
    
    //Private attribut for logging purposes
    private static final Logger logger = Logger.getLogger(DVHandler.class);

    
    /**************************************/
    /************ CONSTRUCTORS *************/
    /**************************************/
    public DVHandler(Routeur currentRouteur)
    {
            this.myRouteur = currentRouteur;
            this.tableRoutageDV = myRouteur.getTableRoutageDV();
            this.coutRoutageDV = myRouteur.getCoutRouteurDV();
            this.routeurVoisin = trouverVoisin(currentRouteur.getNomRouteur());
    }    

    public DVHandler(UDPPacket packetRecu, Routeur currentRouteur){   

            // TODO Auto-generated constructor stub
            this.packetRecu = packetRecu;
            this.myRouteur = currentRouteur;
            this.tableRoutageDV=currentRouteur.getTableRoutageDV();
            this.coutRoutageDV = myRouteur.getCoutRouteurDV();
            this.routeurVoisin = trouverVoisin(currentRouteur.getNomRouteur());
            this.neighborTable = traduireTableRoutageDVPourTransert((Hashtable <Integer,String>) utils.Marshallizer.unmarshallHashtableIntegerString(packetRecu));
           
    }

    /****AUCUN GETTER ET SETTER, USAGE INTERNE SEULEMENT******/
    /****************** GETTER AND SETTER ********************/
    /****AUCUN GETTER ET SETTER, USAGE INTERNE SEULEMENT******/
    
    
    /**************************************/
    /*************   METHODS  *************/
    /**************************************/
    //Permet de trouver tous les voisins d'un routeur
    private synchronized Hashtable<String,Routeur> trouverVoisin(String routeurSource){

        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": trouverVoisin():Permet de trouver les voisins d'un routeur source: ");

        Hashtable<String,Routeur> routeurVoisin = new Hashtable<String,Routeur>();

        for (Arc value : myRouteur.getListeArcs().values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurB().getNomRouteur(),value.getRouteurB());
            }
            if(( value.getRouteurB().getNomRouteur().equals(routeurSource) )){
                routeurVoisin.put(value.getRouteurA().getNomRouteur(),value.getRouteurA());
            }
        }
        return routeurVoisin;
    }

    
    //Permet de trouver le cout entre deux routeurs
    private synchronized int trouverCoutPour(String routeurA, String routeurB){
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": trouverCoutPour(): trouve le côut pour l'arc qui relie " + routeurA + " et " + routeurB);
        for (Arc value : this.myRouteur.getListeArcs().values()) {
            if(( value.getRouteurA().getNomRouteur().equals(routeurA) && value.getRouteurB().getNomRouteur().equals(routeurB) ) || ( value.getRouteurA().getNomRouteur().equals(routeurB) && value.getRouteurB().getNomRouteur().equals(routeurA) )){
                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": trouverCoutPour(): Le cout pour l'arc qui relie " + routeurA + " et " + routeurB + " est de: " + value.getCout());
                return value.getCout();                
            }
        }
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": trouverCoutPour(): aucun arc trouvé entre " + routeurA + " et " + routeurB);
        return -1; // retourne -1 si l'arc n'existe pas
    }

    
    //Permet de trouver le nom du routeur
    private Routeur trouverRouteurViaPort(Integer port){
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": trouverRouteurViaPort(): trouve l'objet routeur qui correspond au port: " + port.toString());

        for (Routeur routeur : this.myRouteur.getListeRouteurs().values()) {
                if(routeur.getPort() == port) return routeur;
        }	
        return null; // retourne null si le port n'est pas présent dans le réseau
    }
    

    //Permet de fabriquer un paquet UDPPacket
    private UDPPacket buildPacket(int gatewayDestinationPort, byte[] data) {
        logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ "  creation du paquet pour le update.");
        UDPPacket packet = new UDPPacket(UDPPacket.UPDATE,gatewayDestinationPort,this.myRouteur.getPort());         
        packet.setData(data);                
        logger.debug(packet.toString());
        return packet;
    }


    //Permet l'envoi d'un udpPacket
    private void sendPacket(UDPPacket udpPacket) {
        try {
                logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": sendPacket executed");
                logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": sendPacket : " + udpPacket.toString());                
                byte[] packetData = Marshallizer.marshallize(udpPacket);
                DatagramPacket datagram = new DatagramPacket(packetData,
                                packetData.length, 
                                udpPacket.getDestination(),
                                udpPacket.getDestinationPort()); // port de la passerelle par default
                this.myRouteur.getRouteurSocket().send(datagram); // émission non-bloquante
        } catch (SocketException e) {
                System.out.println("DVHandler-:" + myRouteur.getNomRouteur()+ " Socket: " + e.getMessage());
        } catch (IOException e) {
                System.out.println("DVHandler-:" + myRouteur.getNomRouteur()+ " IO: " + e.getMessage());
        }
    }


    //Permet de fabriquer un hashtable qui est stream friendly
    private Hashtable<Integer,String> tableRoutageDVPourTransfert(){

        logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": tableRoutageDVPourTransfert() executed");

        Hashtable <Integer,String> tablePourExport = new Hashtable<Integer,String>();

        //Création d'un set pour parcourir la Hashtable
        Set set = this.tableRoutageDV.entrySet();

        //Création d'un iterator pour parcourir notre set
        Iterator it = set.iterator();

        //Boucle while qui parcours le set.
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();

          Routeur tempRouteur = (Routeur) entry.getValue();
          Integer tempKey = (Integer) entry.getKey();

          //On ajoute l'élément à la table pour l'export
          tablePourExport.put(tempKey, tempRouteur.getNomRouteur());
        }
        logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": tableRoutageDVPourTransfert() un Hashtable stream friendly a été crée.");
        return tablePourExport;
    }
    
    
    //Permet de traduire la table recu afin de retrouver nos référence vers les routeurs.
    private Hashtable<Integer,Routeur> traduireTableRoutageDVPourTransert(Hashtable<Integer, String> extractedTable) {
	logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": traduireTableRoutageDVPourTransert() executed");	
        Hashtable <Integer,Routeur> tablePourUpdate = new Hashtable<Integer,Routeur>();

        //Création d'un set pour parcourir la Hashtable
        Set set = extractedTable.entrySet();

        //Création d'un iterator pour parcourir notre set
        Iterator it = set.iterator();

        //Boucle while qui parcours le set.
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          String tempRouterName = (String) entry.getValue();
          Integer tempPort = (Integer) entry.getKey();
          Routeur tempRouteur = this.myRouteur.getListeRouteurs().get(tempRouterName);

          tablePourUpdate.put(tempPort, tempRouteur);

        }
        logger.info("DVHandler-:" + myRouteur.getNomRouteur()+ ": la traduction à bien été effectué.");
        return tablePourUpdate;
    }

    
    //Permet d'envoyer notre table à tous nos voisins
    private void sendTableToNeighbours() {
            logger.info("DVHandler:" + myRouteur.getNomRouteur()+ " sendTableToNeighbours() executed.");
            Enumeration<Routeur> nb = this.tableRoutageDV.elements();
            logger.info("DVHandler:" + myRouteur.getNomRouteur()+ " sendTableToNeighbours() construction du paquet.");

            Hashtable <Integer,String> tablePourExport  = tableRoutageDVPourTransfert();

            byte[] byteArrayTablePourExport = utils.Marshallizer.marshallize(tablePourExport);

            //Construction du paquet
            for (Routeur routeur : this.routeurVoisin.values()) {
                logger.info("DVHandler:" + myRouteur.getNomRouteur()+ " sendTableToNeighbours() envoi de la table vers: " + routeur.getNomRouteur());
                UDPPacket tableUpdate = buildPacket(routeur.getPort(),byteArrayTablePourExport);
                sendPacket(tableUpdate);
            }		
    }    
    
    
    //Permet de initialiser notre table de routage
    private void initTable()
    {
            logger.info("DVHandler:" + myRouteur.getNomRouteur()+ " Initialisation de la table de routage DV.");		
            //Nous initialisons les côuts vers nos routeurs voisins.
            for (Routeur routeur : this.routeurVoisin.values()) {
                //Chemin initiale pour se rendre à nos voisins
                this.tableRoutageDV.put(routeur.getPort(), routeur);

                //Ajout du côut initiale vers nos routeur voisin
                this.coutRoutageDV.put(routeur.getNomRouteur(),trouverCoutPour(this.myRouteur.getNomRouteur(),routeur.getNomRouteur()));
            }
            logger.info("Routeur:" + myRouteur.getNomRouteur()+ " Initialisation de la table de routage DV terminé.");
    }
    
    
    //Permet de mettre à jour notre table
    private void updateTable(){        
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() est en cours d'éxécution.");
    
        Routeur routeurVoisinExpediteur = trouverRouteurViaPort(this.packetRecu.getSourcePort());
 
        //On parcours la table reçu
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() parcours de la table de: " + routeurVoisinExpediteur.getNomRouteur());
        for (Routeur routeur : this.neighborTable.values()) {
            
            //Cout du routeur courant vers son voisin
            Integer coutVersVoisin = this.coutRoutageDV.get(routeurVoisinExpediteur.getNomRouteur());
                
            //Cout du routeurVoisin vers la nouvelle destination
            Integer coutVoisinVersDestination = routeurVoisinExpediteur.getCoutRouteurDV().get(routeur.getNomRouteur());
            
            //On valide si l'entrée est présente dans notre table
            if(!this.tableRoutageDV.containsKey(routeur.getPort())){
                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() ajout d'une route dans tableRoutageDV vers: " + routeur.getNomRouteur() + " via " + routeurVoisinExpediteur.getNomRouteur());

                //L'entrée n'est pas présente alors on l'ajoutons avec son côut
                this.tableRoutageDV.put(routeur.getPort(), routeurVoisinExpediteur);                
                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": coutVerVoisin: " + coutVersVoisin.toString());
                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": coutVoisinversDest: " + coutVoisinVersDestination.toString());
                //Nous ajoutons le côuts a notre table de cout
                //Le cout correspond a notre cout vers le voisin + le cout du voisin vers la destination
                this.coutRoutageDV.put(routeur.getNomRouteur(), coutVersVoisin + coutVoisinVersDestination );                

                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() ajout d'un cout dans coutRoutageDV de: " + this.coutRoutageDV.get(routeur.getNomRouteur()).toString() + " vers " + routeur.getNomRouteur());

                 //On signale quon a fait une modification
                tableRoutageDVWasEdited = true;
            }
            else{
                //L'entrée existe déja dans notre table de routage  
                
                //On vérifie si notre côut courant est supérieur à celui reçu
                if(this.coutRoutageDV.get(routeur.getNomRouteur()) > coutVersVoisin + coutVoisinVersDestination ){
                    //Le coût de notre routeur vers la destination est supérieur, alors on le remplace
                    
                    //L'entrée n'est pas présente alors on l'ajoutons avec son côut
                    this.tableRoutageDV.replace(routeur.getPort(), routeurVoisinExpediteur); 
                    
                    logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() modification de la route dans tableRoutageDV vers: " + routeur.getNomRouteur() + " passe maintenant via " + routeurVoisinExpediteur.getNomRouteur());

                    //Le cout correspond a notre cout vers le voisin + le cout du voisin vers la destination
                    this.coutRoutageDV.replace(routeur.getNomRouteur(), coutVersVoisin + coutVoisinVersDestination );

                    logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() modification du cout vers: " + routeur.getNomRouteur()  + " le côut est maintenant de: " + this.coutRoutageDV.get(routeur.getNomRouteur()).toString());

                    //On signale quon a fait une modification
                    tableRoutageDVWasEdited = true;
                }
            }            
        }            
    }
    
    
    /**************************************/
    /*************   THREAD   *************/
    /**************************************/
    private void start() {
            logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": new runnable. ");

            // TODO Auto-generated method stub
            try
            {
                //Si aucune route est présente dans notre table de routage. 
                if ( tableRoutageDV.isEmpty()){	
                        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": tableRoutageDV est vide, donc initTable() sera appelé. "); 
                        //Aucune route présente dans la table, alors nous initialisons le routeur pour DV.
                        initTable();
                        Thread.currentThread().yield();
                         //Timer pour l'attente du routage
                        /*Timer WaitBeforeSendTimer = new Timer(); //Timer pour les timeouts
                        WaitBeforeSendTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendTableToNeighbours();
                            }
                          }, 3000);  */
                        sendTableToNeighbours();
                }
                else
                {
                    logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": tableRoutageDV est non vide, donc updateTable() sera appelé. "); 

                    //Il existe des routes pour le routeur courant                        
                    updateTable();

                    //Si la table a été modifié on envoit les modifications aux voisins
                    if(tableRoutageDVWasEdited) sendTableToNeighbours();
                    else{
                      logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": aucune MaJ pour la tableRoutageDV. "); 
                      logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": tableRoutageDV: " + this.tableRoutageDV.toString()); 
                    }
                }

            }
            catch (Exception e) 
            {
                    System.out.println("DVHandler-EXCEPTION-"+ this.myRouteur.getNomRouteur() +": " + e.getMessage());
            }
            finally {
                logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": Fin du thread DVHandler. ");                
            }

    }
    @Override
    public void run() {
            start();
            // TODO Auto-generated method stub
    }
}