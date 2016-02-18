/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import protocole.UDPPacket;
import utils.Marshallizer;

public class Routeur implements Runnable ,  Serializable{
	
	private static final long serialVersionUID = 2622663736791338175L;

	/**************************************/
	/********* PRIVATE ATTRIBUTS **********/
	/**************************************/    
	private String nomRouteur;
	private transient DatagramSocket routeurSocket = null;
	private transient DatagramPacket packetReceive;
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
	private Hashtable <Integer,Routeur> receivedTable;
	private Integer portVoisin;



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

	public Hashtable<Integer, Routeur> getTableRoutageDV() {
		return tableRoutageDV;
	}

	public void setTableRoutageDV(Hashtable<Integer, Routeur> tableRoutageDV) {
		this.tableRoutageDV = tableRoutageDV;
	}

	public Integer getPortVoisin() {
		return portVoisin;
	}

	public void setPortVoisin(Integer portVoisin) {
		this.portVoisin = portVoisin;
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
		logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): trouve le c�ut pour l'arc qui relie " + routeurA + " et " + routeurB);
		for (Arc value : listeArcs.values()) {
			if(( value.getRouteurA().getNomRouteur().equals(routeurA) && value.getRouteurB().getNomRouteur().equals(routeurB) ) || ( value.getRouteurA().getNomRouteur().equals(routeurB) && value.getRouteurB().getNomRouteur().equals(routeurA) )){
				logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): Le cout pour l'arc qui relie " + routeurA + " et " + routeurB + " est de: " + value.getCout());
				return value.getCout();                
			}
		}
		logger.info("Routeur-" + this.getNomRouteur() +": trouverCoutPour(): aucun arc trouv� entre " + routeurA + " et " + routeurB);
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


	//Permet de trouver le routeur qui a le c�ute D(w) le plus bas.
	private synchronized String trouverPlusPetitDW(Hashtable<String, Routeur> listeW){
		logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): on trouve le w le avec le plus petit D(w)");
		int indice = 100000;
		String nom = "";
		//logger.info("Routeur-" + this.getNomRouteur() +": trouverPlusPetitDW(): int�rieur:" + listeW.toString());

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


	//Permet de d�duire la table de routage Ls suite au calcul LS.
	private synchronized void calculerTableRoutageLS(Hashtable<String, Routeur> cloneListe){
		logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): Suite � l'algorithme utilis� en calculPourLs(), nous d�duisons la table de routage LS.");

		for (Routeur routeurCourant : cloneListe.values()) {
			if(!routeurCourant.getNomRouteur().equals(this.getNomRouteur())){ 
				logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): D�but calcul pour la destination: " + routeurCourant.getNomRouteur());

				Routeur fowardRouteur = trouverFoward(cloneListe,routeurCourant);
				ajouterRouteTableRoutageLS(routeurCourant.getPort(),fowardRouteur);
				logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): pour se rendre �: " + routeurCourant.getNomRouteur() + " on foward vers: " + fowardRouteur.getNomRouteur());
			}
		} 

		logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): la table de routage a �t� g�n�r�.");
		logger.info("Routeur-" + this.getNomRouteur() +": calculerTableRoutageLS(): table de routage: " + this.getTableRoutageLS().toString());
	}


	//Fonction r�cursive qui nous permet de trouver le routeur � qui nous devons transferer
	private synchronized Routeur trouverFoward(Hashtable<String, Routeur> cloneListe, Routeur r){
		logger.info("Routeur-" + this.getNomRouteur() +": trouverFoward(): Nous cherchons le pr�d�cesseur de: " + r.getNomRouteur());

		if(r.getPredecesseurRouteurLS().equals(this.getNomRouteur()) ) return r;        
		else return trouverFoward(cloneListe,cloneListe.get(r.getPredecesseurRouteurLS()));
	}


	//Permet de trouver le chemin optimale pour l'instance du routeur.
	private synchronized void calculPourLs(){
		logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): D�but de l'algorithme pour trouver les chemins les plus courts");

		//Ajout du chemin le plus court pour le routeur source
		ajouterRouteTableRoutageLS(this.getPort(),this);

		//Ajout du source dans N
		N.put(this.getNomRouteur(),this);

		//Routeur voisin de source
		Hashtable<String,Routeur> routeurVoisin = trouverVoisin(this.getNomRouteur());

		logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): initialisation des c�uts D(v) pour le routeur: " + this.getNomRouteur());

		//On �vite que tous les threads modifie la m�me liste.
		Hashtable<String, Routeur> cloneListe = new Hashtable<String, Routeur>();
		for (Routeur routeurCourant : listeRouteurs.values()) {            
			cloneListe.put(routeurCourant.getNomRouteur(), new Routeur(routeurCourant));
		}

		//STEP 1, on met tous � infini et on met les couts pour nos voisins
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


			//On trouve le routeur avec la pod�ration la plus petite
			String w = trouverPlusPetitDW(cloneListe);
			Routeur rW = cloneListe.get(w);

			//On ajoute le routeur dans notre liste de routeur ayant le chemin le plus optimale
			N.put(w, rW); // On ajoute le routeur courant � la liste N

			logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): N � �t� MaJ. le routeur: "+ rW.getNomRouteur() + " � comme pr�d�cesseur: " + rW.getPredecesseurRouteurLS());

			//On r�cupere les voisins de w, qui ne sont pas d�ja dans N
			routeurVoisin = trouverVoisinNonN(w);

			//Corrige les pointeurs
			for (Routeur r : routeurVoisin.values()) {
				routeurVoisin.replace(r.getNomRouteur(), cloneListe.get(r.getNomRouteur()));
			} 

			logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): recupere les voisins de w");

			//On r�cupere le routeur en lien avec w.

			for (Routeur routeurCourant : routeurVoisin.values()) {
				//On v�rifie si l'indice de cout d(v) est plus petit que l'addition de d(w) + c(w,v)
				if(routeurCourant.getIndiceCoutLS() > ( rW.getIndiceCoutLS() + trouverCoutPour(w,routeurCourant.getNomRouteur()) ) ){
					//l'indice du chemin via w, est inf�rieur alors on met � jour
					routeurCourant.setIndiceCoutLS(rW.getIndiceCoutLS() + trouverCoutPour(w,routeurCourant.getNomRouteur()));
					routeurCourant.setPredecesseurRouteurLS(w);
				}                
			} 
			logger.info("Routeur-" + this.getNomRouteur() +": N ressemble �: " + N.toString());    
		}while(N.size() != cloneListe.size());

		logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): calcul termin�.");
		logger.info("Routeur-" + this.getNomRouteur() +": calculPourLs(): cr�ation de la table de routage avec les donn�es obtenues.");

		//STEP 3
		calculerTableRoutageLS(cloneListe); 
	}


	//Permet de foward un paquet vers la destination re�u en param
	private void sendPacket(UDPPacket udpPacket, int destinationPort) {
		try {
			logger.info("Routeur-" + this.getNomRouteur() + ": sendPacket executed");
			logger.info("Routeur-" + this.getNomRouteur() + ": sendPacket : " + udpPacket.toString());                
			byte[] packetData = Marshallizer.marshallize(udpPacket);
			DatagramPacket datagram = new DatagramPacket(packetData,
					packetData.length, 
					udpPacket.getDestination(),
					destinationPort); // port de la passerelle par default
			routeurSocket.send(datagram); // �mission non-bloquante
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
			logger.info("Routeur-" + this.getNomRouteur()+ " a �t� d�marr� sur le port: " + this.getPort());
			routeurSocket = new DatagramSocket(this.getPort()); // port pour l'envoi et l'�coute                

			byte[] buffer = new byte[1500];

			packetReceive = new DatagramPacket(buffer, buffer.length); 

			if(typeRoutage == Reseau.LSROUTING){
				//G�n�ration des meilleurs chemins avec LS               
				logger.info("Routeur-" + this.getNomRouteur() + " utilise un routage de type LS (LINK-STATE)");

				calculPourLs();               
			}
			if(typeRoutage == Reseau.DVROUTING){
				//Initiation des tables de routage pour DV
				logger.info("Routeur-" + this.getNomRouteur() + " utilise un routage de type DV (DISTANCE VECTOR)");
				Thread DVThread = new Thread(new DVHandler(this));
				DVThread.start();
				logger.info("Routeur: Update Thread Started on " + this.getNomRouteur());

			}             

			//D�but de la boucle pour recevoir des paquets
			do  {                   

				logger.info("Routeur-" + this.getNomRouteur() + ": waiting for a packet");
				routeurSocket.receive(packetReceive); // reception bloquante
				logger.info("Routeur-" + this.getNomRouteur() + ": a packet was receive");                   

				UDPPacket packet = (UDPPacket) Marshallizer.unmarshall(packetReceive);

				//Paquet de type FOWARD
				if(packet.getType() == UDPPacket.FOWARD){

					logger.info("Routeur-" + this.getNomRouteur() + ": a re�u un paquet de type foward");
					logger.info("Routeur-" + this.getNomRouteur() + ": on regarde si la destination est dans notre table d'h�te");

					if(tableRoutageHote.containsKey(packet.getDestinationPort())){
						logger.info("Routeur-" + this.getNomRouteur() + ": la destination est dans notre table d'h�te");

						sendPacket(packet,packet.getDestinationPort());
						logger.info("Routeur-" + this.getNomRouteur() + ": le paquet � �t� remis � l'h�te: " + tableRoutageHote.get(packet.getDestinationPort()));

					}
					else
					{
						logger.info("Routeur-" + this.getNomRouteur() + ": la destination n'est pas dans notre table d'h�te. Alors on FOWARD."); 
						/********************************************************************************************************************************/ 
						/*******  IL VA FALLOIR MODIFIER LA VALEUR ELSE POUR portDestination et destinataire, afin d'utiliser la tableRoutageDV   *******/ 
						/********************************************************************************************************************************/
						int portDestination = (typeRoutage == Reseau.LSROUTING) ?  tableRoutageLS.get(packet.getDestinationGatewayPort()).getPort() : null;
						String destinataire = (typeRoutage == Reseau.LSROUTING) ?  tableRoutageLS.get(packet.getDestinationGatewayPort()).getNomRouteur() : null;
						/********************************************************************************************************************************/ 
						/*******  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^   *******/ 
						/********************************************************************************************************************************/
						sendPacket(packet,portDestination);
						logger.info("Routeur-" + this.getNomRouteur() + ": le paquet � �t� transmis �: " + destinataire);

					}                        

					logger.info("Routeur-" + this.getNomRouteur() + " le packet re�u � �t� bien �t� transmis.");   
				}
				//Paquet de type UPDATE pour un DVHandler
				if(packet.getType() == UDPPacket.UPDATE){
					//ON START THREAD MAJ TABLE 
					extraireTable(packet);
					portVoisin = packet.getSourcePort();
					Thread DVThread = new Thread(new DVHandler(receivedTable, this));
					DVThread.start();
					logger.info("Routeur: Update Thread Started on " + this.getNomRouteur());
				}
			}while (true);

		} catch (Exception e) {
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			logger.info("Routeur-" + this.getNomRouteur() +" Fin du thread.");                
		}
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

	@Override
	public void run() {
		start();	
	}  

}