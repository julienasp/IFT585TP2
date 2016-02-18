/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import org.apache.log4j.Logger;

import protocole.UDPPacket;
import utils.Marshallizer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;

public class Routeur implements Runnable {
	private Reseau monReseau;
	//private Integer nbVoisins;
	//private Integer nbElemtsReseau;
	private String nomRouteur;
	private Integer port;
	private Integer portVoisin;
	private int coutInfini = 1000000;
	private Hashtable <String,Arc> ArcToVoisins=new Hashtable <String,Arc>();
	private Hashtable <Integer,Routeur> bestPath = new Hashtable <Integer,Routeur>();
	private Hashtable <Routeur,Integer> coutRouteur = new Hashtable <Routeur,Integer>();
	private DatagramPacket packetReceive;
	private Hashtable <Integer,Routeur> receivedTable;
	private DatagramSocket waitingMessage;
	private int typeRoutage;
	private Hashtable<String, Hote> listeHotes = new Hashtable<String,Hote>();
	private Hashtable<Integer,Hote> tableRoutageHote = new Hashtable<Integer,Hote>();
	private Hashtable<String, Arc> touslesArcs;
	private Hashtable<String, Routeur> touslesRouteurs;

	public Hashtable<String, Routeur> getTouslesRouteurs() {
		return touslesRouteurs;
	}

	public void setTouslesRouteurs(Hashtable<String, Routeur> touslesRouteurs) {
		this.touslesRouteurs = touslesRouteurs;
	}

	public Hashtable<String, Arc> getTouslesArcs() {
		return touslesArcs;
	}

	public void setTouslesArcs(Hashtable<String, Arc> touslesArcs) {
		this.touslesArcs = touslesArcs;
	}

	public int getTypeRoutage() {
		return typeRoutage;
	}

	public void setTypeRoutage(int typeRoutage) {
		this.typeRoutage = typeRoutage;
	}

	public void ajouterHote(Hote unHote) {
		listeHotes.put(unHote.getNomHote(), unHote);
	}

	public void retirerHote(String nomHote) {
		listeHotes.remove(nomHote);
	}

	public void ajouterHoteTableRoutage(int portDestitation,Hote unHote) {
		tableRoutageHote.put(portDestitation, unHote);
	}

	public synchronized void retirerHoteTableRoutage(int portDestitation) {
		tableRoutageHote.remove(portDestitation);
	}
	//Private attribut for logging purposes
	private static final Logger logger = Logger.getLogger(Routeur.class);

	public Routeur()
	{

	}

	public Routeur(String nomRouteur, int port) {
		this.nomRouteur = nomRouteur;
		this.port = port;
	}

	public Integer getPort() {
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

	public void trouverVoisin()
	{
		//touslesArcs = monReseau.getListeArcs();
		Enumeration<Arc> nb = touslesArcs.elements();

		while(nb.hasMoreElements()) 
		{
			Arc key = nb.nextElement(); 
			Arc current = (Arc)touslesArcs.get(key.getNomArc());
			
			
			if ( this.nomRouteur == current.getRouteurA().getNomRouteur() )
			{
				ArcToVoisins.put(current.getRouteurB().getNomRouteur(), current);
				bestPath.put(current.getRouteurB().getPort(), current.getRouteurB());
				coutRouteur.put(current.getRouteurB(),current.getCout());
				logger.info("Routeur"+ this.getNomRouteur() + ": Neighbor " + current.getRouteurB().getNomRouteur()+ " added to table");
			}

			else if (this.nomRouteur == current.getRouteurB().getNomRouteur()  )
			{
				
				ArcToVoisins.put(current.getRouteurA().getNomRouteur(), current);
				bestPath.put(current.getRouteurA().getPort(), current.getRouteurA());
				coutRouteur.put(current.getRouteurB(),current.getCout());
				logger.info("Routeur: Neighbor " + current.getRouteurA().getNomRouteur()+ " added to table");
			}
		}
	}

	public Hashtable<String, Arc> getArcToVoisins() {
		return ArcToVoisins;
	}

	public void setArcToVoisins(Hashtable<String, Arc> arcToVoisins) {
		ArcToVoisins = arcToVoisins;
	}

	public Hashtable<Integer, Routeur> getBestPath() {
		return bestPath;
	}

	public void setBestPath(Hashtable<Integer, Routeur> bestPath) {
		this.bestPath = bestPath;
	}

	public Hashtable<Routeur, Integer> getCoutRouteur() {
		return coutRouteur;
	}

	public void setCoutRouteur(Hashtable<Routeur, Integer> coutRouteur) {
		this.coutRouteur = coutRouteur;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void initTable()
	{
		//Hashtable<String, Routeur> touslesRouteurs = monReseau.getListeRouteurs();
		Enumeration<Routeur> nb = touslesRouteurs.elements();

		while(nb.hasMoreElements()) 
		{
			Routeur key = nb.nextElement(); 
			Routeur currentRouteur = (Routeur)touslesRouteurs.get(key.getNomRouteur());
			
			if(!coutRouteur.containsKey(currentRouteur))
			{
				logger.info("Routeur " +this.getNomRouteur()+ " : Routeur " + currentRouteur.getNomRouteur()+ " non voisin");
				coutRouteur.put(currentRouteur, coutInfini);
				bestPath.put(currentRouteur.getPort(), currentRouteur);
				logger.info("Routeur" +this.getNomRouteur()+ " : Cout vers " + currentRouteur.getNomRouteur()+ " mis a l'infini");
			}
		}
	}

	public void start() {		

		try 
		{
			waitingMessage = new DatagramSocket (port);
			logger.info("Routeur-" + this.getNomRouteur() + " Opening socket on " + this.getPort()); 

			trouverVoisin();
			logger.info("Routeur: Les voisins ont ete ajoutes a la table");

			initTable();
			logger.info("Routeur: Initialized table");
			byte[] buffer = new byte[1500];

			packetReceive = new DatagramPacket(buffer, buffer.length);
			// on recoit packet d'un voisin 
			while (true)
			{
				logger.info("Routeur: Waiting for neighbor packet on " + this.getNomRouteur());
				waitingMessage.receive(packetReceive);
				logger.info("Routeur: Neighbor packet received on " + this.getNomRouteur());

				UDPPacket udpPacket = (UDPPacket) Marshallizer.unmarshall(packetReceive);
				portVoisin = udpPacket.getSourcePort();

				if (udpPacket.isForFoward())
				{
					logger.info("Routeur: Forward Packet Received  on " + this.getNomRouteur());
					logger.info("Routeur-" + this.getNomRouteur() + ": looking if destination already exists in table");
					logger.info("Routeur-" + this.getNomRouteur() + ": port Voisin" +portVoisin );
					
					if(bestPath.containsKey(9005))
					{
						//On envoie au routeur concerne
						logger.info("Routeur-" + this.getNomRouteur() + ": destination already in table");

						//logger.info("Routeur: Forward Thread Started  on " + this.getNomRouteur());

						sendPacket(udpPacket,udpPacket.getDestinationPort());
						logger.info("Routeur-" + this.getNomRouteur() + ": le paquet à été remis à l'hôte: " + bestPath.get(udpPacket.getDestinationPort()));
					}
					else
					{
						for (Routeur routeurCourant : bestPath.values())
						{
							logger.info("Routeur-" + this.getNomRouteur() + " MON NOM EST" + routeurCourant.getNomRouteur());
							logger.info("Routeur-" + this.getNomRouteur() + " MON PORT EST" + routeurCourant.getPort());
							logger.info("Routeur-" + this.getNomRouteur() + " MON PORT DESTINATION EST" + udpPacket.getDestinationPort());
							
						} 
					}
					logger.info("Routeur-" + this.getNomRouteur() + " packet sent."); 
				}
				else if(udpPacket.isForUpdate())
				{
					logger.info("Routeur: Update Packet Received on " + this.getNomRouteur());
					extraireTable(udpPacket);
					logger.info("Routeur-" + this.getNomRouteur() + " La table a ete extraite du packet."); 

					//ON START THREAD MAJ TABLE 
					Thread DVThread = new Thread(new DVHandler(receivedTable, this));
					DVThread.start();
					logger.info("Routeur: Update Thread Started on " + this.getNomRouteur());
				}
				else 
				{
					logger.info("Routeur: udpPacket isn't for Update OR Forward: on " + this.getNomRouteur());
				}

			}
			//waitingMessage.close();

		} catch (Exception e) {
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			logger.info("Fin de l'initialisation du reseau");                
		}

	}  

	public Integer getPortVoisin() {
		return portVoisin;
	}

	public void setPortVoisin(Integer portVoisin) {
		this.portVoisin = portVoisin;
	}

	public void extraireTable(UDPPacket receivedPacket)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(receivedPacket.getData());
		ObjectInput in = null;
		Hashtable <Integer,Routeur> extractedTable; 
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject(); 
			extractedTable = (Hashtable <Integer,Routeur>)o;

			this.receivedTable=extractedTable;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception

			}
		}
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
			waitingMessage.send(datagram); // émission non-bloquante
		} catch (SocketException e) {
			System.out.println("Routeur-" + this.getNomRouteur() + " Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Routeur-" + this.getNomRouteur() + " IO: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		start();	
	}  

}
