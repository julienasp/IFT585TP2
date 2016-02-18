package dataObject;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import protocole.UDPPacket;
import utils.Marshallizer;


public class DVHandler implements Runnable {

	private Hashtable <Integer,Routeur> neighborTable;
	private Routeur myRouteur;
	private String nomEnvoyeur;
	private Hashtable <Routeur,Integer> coutRouteur = new Hashtable<Routeur,Integer>();
	private Hashtable <Integer,Routeur> bestPath ;
	private Hashtable <String,Arc> ArcToVoisins = new Hashtable<String,Arc>();
	private int coutInfini = 1000000;
	private UDPPacket giftToNeighbor;
	private byte[] yourBytes;


	private static final Logger logger = Logger.getLogger(Routeur.class);


	public DVHandler(Routeur currentRouteur)
	{
		this.myRouteur = currentRouteur;
		this.bestPath=myRouteur.getTableRoutageDV();
	}

	public DVHandler(Hashtable<Integer, Routeur> receivedDVTable, Routeur currentRouteur)
	{
		// TODO Auto-generated constructor stub
		this.neighborTable = receivedDVTable;
		this.myRouteur = currentRouteur;
		this.bestPath=currentRouteur.getTableRoutageDV();
	}

	public void construireTable(Hashtable<Integer, Routeur> tableRecue, String nomEnvoyeur)
	{
		//Voisin ayant envoye sa table
		Arc currentVoisinArc = ArcToVoisins.get(nomEnvoyeur);
		Routeur currentVoisin ;

		if(currentVoisinArc.getRouteurA().getNomRouteur() == nomEnvoyeur)
		{
			currentVoisin = currentVoisinArc.getRouteurA();
			logger.info("Routeur: Neighbor is " + currentVoisinArc.getRouteurA().getNomRouteur());
		}
		else 
		{
			currentVoisin = currentVoisinArc.getRouteurB();
			logger.info("Routeur: Neighbor is " + currentVoisinArc.getRouteurB().getNomRouteur());
		}

		//ON Parcout les elements de la table recue
		Enumeration<Routeur> nb = tableRecue.elements();
		while(nb.hasMoreElements()) 
		{
			Object key = nb.nextElement(); 
			//Routeur vers lequel on rajoute un chemin
			Routeur currentNew = (Routeur)tableRecue.get(key);

			if ( currentNew.getPort() != myRouteur.getPort() && this.bestPath.containsKey(key) ==false)
			{
				this.bestPath.put(currentNew.getPort(), currentNew);
				logger.info("Routeur: Chemin non existant. Chemin ajoute vers " + currentNew.getNomRouteur());
			}

			else if (currentNew.getPort() != myRouteur.getPort() && this.bestPath.containsKey(key) ==true )
			{
				if( currentVoisinArc.getCout() + this.coutRouteur.get(currentNew)  < this.coutRouteur.get(currentNew))
				{
					this.bestPath.put(currentNew.getPort(), currentVoisin);
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

	@Override
	public void run() {
		start();
		// TODO Auto-generated method stub
	}

	public void initTable()
	{
		//Hashtable<String, Routeur> touslesRouteurs = monReseau.getListeRouteurs();
		Enumeration<Routeur> nb = myRouteur.getListeRouteurs().elements();

		while(nb.hasMoreElements()) 
		{
			Routeur key = nb.nextElement(); 
			Routeur currentRouteur = (Routeur)myRouteur.getListeRouteurs().get(key.getNomRouteur());

			if(!coutRouteur.containsKey(currentRouteur))
			{
				logger.info("Routeur " +myRouteur.getNomRouteur()+ " : Routeur " + currentRouteur.getNomRouteur()+ " non voisin");
				coutRouteur.put(currentRouteur, coutInfini);
				bestPath.put(currentRouteur.getPort(), currentRouteur);
				logger.info("Routeur" +myRouteur.getNomRouteur()+ " : Cout vers " + currentRouteur.getNomRouteur()+ " mis a l'infini");
			}
		}
	}

	public void trouverVoisin()
	{
		//touslesArcs = monReseau.getListeArcs();
		Enumeration<Arc> nb = myRouteur.getListeArcs().elements();

		while(nb.hasMoreElements()) 
		{
			Arc key = nb.nextElement(); 
			Arc current = (Arc)myRouteur.getListeArcs().get(key.getNomArc());

			if ( myRouteur.getNomRouteur() == current.getRouteurA().getNomRouteur() )
			{
				this.ArcToVoisins.put(current.getRouteurB().getNomRouteur(), current);
				this.bestPath.put(current.getRouteurB().getPort(), current.getRouteurB());
				this.coutRouteur.put(current.getRouteurB(),current.getCout());
				logger.info("Routeur"+ myRouteur.getNomRouteur() + ": Neighbor " + current.getRouteurB().getNomRouteur()+ " added to table");
			}

			else if (myRouteur.getNomRouteur() == current.getRouteurB().getNomRouteur()  )
			{
				this.ArcToVoisins.put(current.getRouteurA().getNomRouteur(), current);
				this.bestPath.put(current.getRouteurA().getPort(), current.getRouteurA());
				this.coutRouteur.put(current.getRouteurB(),current.getCout());
				logger.info("Routeur" + myRouteur.getNomRouteur() + ": Neighbor " + current.getRouteurA().getNomRouteur()+ " added to table");
			}
		}
	}

	public void sendMyTable() throws IOException
	{
		Enumeration<Routeur> nb = this.bestPath.elements();

		while(nb.hasMoreElements()) 
		{
			Routeur key = nb.nextElement(); 
			Routeur currentRouteur = (Routeur)this.bestPath.get(key.getPort());

			if(coutRouteur.get(currentRouteur) != coutInfini)
			{
				giftToNeighbor = new UDPPacket (0,currentRouteur.getPort(),myRouteur.getPort());
				logger.info("Routeur" + myRouteur.getNomRouteur() + ": Sending routing table to " + currentRouteur.getNomRouteur());

				construirePacket(bestPath);
				sendPacket(giftToNeighbor,currentRouteur.getPort());
				logger.info("Routeur" + myRouteur.getNomRouteur() + ": On " + currentRouteur.getPort());
			}
		}
	}

	private void start() {
		// TODO Auto-generated method stub
		try
		{
			if ( bestPath.isEmpty())
			{
				trouverVoisin();
				logger.info("Routeur" + myRouteur.getNomRouteur() + ": Les voisins ont ete ajoutes a la table");

				initTable();
				logger.info("Routeur" + myRouteur.getNomRouteur() + ":  Initialized table");
				sendMyTable();
				logger.info("Routeur" + myRouteur.getNomRouteur() + ": Envoie table aux voisins");
			}
			else
			{
				nomEnvoyeur = this.bestPath.get(myRouteur.getPortVoisin()).getNomRouteur();
				construireTable(this.neighborTable,this.nomEnvoyeur);
			}


		}
		catch (Exception e) 
		{
			System.out.println("IO: " + e.getMessage());
		}

	}

	public void construirePacket(Serializable Object) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		try {
			out.writeObject(Object);
			out.close();
			yourBytes = bos.toByteArray();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		//yourBytes=Marshallizer.marshallize(Object);
	}


	private void sendPacket(UDPPacket udpPacket, int destinationPort) {
		try {
			logger.info("Routeur-" + myRouteur.getNomRouteur() + ": sendPacket executed");
			logger.info("Routeur-" + myRouteur.getNomRouteur() + ": sendPacket : " + udpPacket.toString());
			DatagramPacket datagram = new DatagramPacket(yourBytes,yourBytes.length, InetAddress.getLocalHost(), destinationPort); // port de la passerelle par default
			myRouteur.getRouteurSocket().send(datagram); // émission non-bloquante
		} catch (SocketException e) {
			System.out.println("Routeur-" + myRouteur.getNomRouteur() + " Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Routeur-" + myRouteur.getNomRouteur() + " IO: " + e.getMessage());
		}


	}
}

