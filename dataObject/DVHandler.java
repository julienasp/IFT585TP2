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
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import utils.Marshallizer;


public class DVHandler implements Runnable {

    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private Routeur myRouteur;
    private String nomEnvoyeur;
    private Hashtable<String,Routeur> routeurVoisin;
    private UDPPacket packetRecu;    
    private Hashtable <Integer,Routeur> neighborTable;	
    private Hashtable <Routeur,Integer> coutRoutageDV;
    private Hashtable <Integer,Routeur> tableRoutageDV ;
    private Hashtable <String,Arc> ArcToVoisins = new Hashtable<String,Arc>();
    
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

    public DVHandler(UDPPacket packetRecu, Routeur currentRouteur)
    {
            // TODO Auto-generated constructor stub
            this.packetRecu = packetRecu;
            this.neighborTable = traduireTableRoutageDVPourTransert((Hashtable <Integer,String>) utils.Marshallizer.unmarshallHashtableIntegerString(packetRecu));
            this.myRouteur = currentRouteur;
            this.tableRoutageDV=currentRouteur.getTableRoutageDV();
            this.coutRoutageDV = myRouteur.getCoutRouteurDV();
            this.routeurVoisin = trouverVoisin(currentRouteur.getNomRouteur());
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
    
    
    //Permet de mettre à jour notre table
    private void updateTable(){        
        logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": updateTable() est en cours d'éxécuté.");
    
        Routeur routeurExpediteur = trouverRouteurViaPort(this.packetRecu.getSourcePort());
            
        //On parcours la table reçu
        for (Routeur routeur : this.neighborTable.values()) {
            
            
        }
            //ON Parcout les elements de la table recue
            Enumeration<Routeur> nb = tableRecue.elements();
            while(nb.hasMoreElements()) 
            {
                    Routeur key = nb.nextElement(); 
                    //Routeur vers lequel on rajoute un chemin
                    Routeur currentNew = (Routeur)tableRecue.get(key.getPort());

                    if ( currentNew.getPort() != myRouteur.getPort() && this.tableRoutageDV.containsKey(key.getPort()) ==false)
                    {
                            this.tableRoutageDV.put(currentNew.getPort(), currentNew);
                            logger.info("Routeur"+myRouteur.getNomRouteur()+" : Chemin non existant. Chemin ajoute vers " + currentNew.getNomRouteur());
                    }

                    else if (currentNew.getPort() != myRouteur.getPort() && this.tableRoutageDV.containsKey(key.getPort()) ==true )
                    {
                            logger.info("Routeur: JE BUGGGGGGGGGGGGGGG " + myRouteur.getNomRouteur());
                            if( currentVoisinArc.getCout() + this.coutRoutageDV.get(currentNew)  < this.coutRoutageDV.get(currentNew))
                            {
                                    this.tableRoutageDV.put(currentNew.getPort(), currentVoisin);
                                    logger.info("Routeur: Cout plus petit que cout existant.Chemin ajoute vers " + currentNew.getNomRouteur());
                            }
                            else
                            {
                                    logger.info("Routeur: Pas de chemin ajoute vers "  + currentNew.getNomRouteur() +" Cout plus eleve que cout existant? ");
                            }
                    }
            }
            logger.info("Routeur:" + myRouteur.getNomRouteur()+ " Fin mise a jour de tables");
            /* FIN MISE A JOUR TABLES */
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
                this.coutRoutageDV.put(routeur,trouverCoutPour(this.myRouteur.getNomRouteur(),routeur.getNomRouteur()));
            }
            logger.info("Routeur:" + myRouteur.getNomRouteur()+ " Initialisation de la table de routage DV terminé.");
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
        Set set = this.tableRoutageDV.entrySet();

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

    
    /**************************************/
    /*************   THREAD   *************/
    /**************************************/
    private void start() {
            logger.info("DVHandler-" + this.myRouteur.getNomRouteur() +": new runnable. ");

            // TODO Auto-generated method stub
            try
            {
                //Si aucune route est présente dans notre table de routage. 
                if ( tableRoutageDV.isEmpty())
                    {	
                        //Aucune route présente dans la table, alors nous initialisons le routeur pour DV.
                        initTable();				
                        sendTableToNeighbours();				
                    }
                    else
                    {
                        //Il existe des routes pour le routeur courant                        
                        updateTable();
                    }

            }
            catch (Exception e) 
            {
                    System.out.println("IO: " + e.getMessage());
            }

    }
    @Override
    public void run() {
            start();
            // TODO Auto-generated method stub
    }
}