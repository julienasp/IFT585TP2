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
	private UDPPacket giftToNeighbor = new UDPPacket (0,0,0);
	private Hashtable <Integer,String> toSend  = new Hashtable<Integer,String> ();

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
		Enumeration<Arc>testEnum = ArcToVoisins.elements();
		
		while(testEnum.hasMoreElements())
		{
			Arc curreeeent = testEnum.nextElement();
		}
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
			Routeur key = nb.nextElement(); 
			//Routeur vers lequel on rajoute un chemin
			Routeur currentNew = (Routeur)tableRecue.get(key.getPort());
			
			if ( currentNew.getPort() != myRouteur.getPort() && this.bestPath.containsKey(key.getPort()) ==false)
			{
				this.bestPath.put(currentNew.getPort(), currentNew);
				logger.info("Routeur"+myRouteur.getNomRouteur()+" : Chemin non existant. Chemin ajoute vers " + currentNew.getNomRouteur());
			}

			else if (currentNew.getPort() != myRouteur.getPort() && this.bestPath.containsKey(key.getPort()) ==true )
			{
				logger.info("Routeur: JE BUGGGGGGGGGGGGGGG " + myRouteur.getNomRouteur());
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
		Enumeration<Arc> nb = myRouteur.getListeArcs().elements();
		while(nb.hasMoreElements()) 
		{
			//Parcourt tous les arcs et trouve les voisins
			Arc key = nb.nextElement(); 
			Arc current = (Arc)myRouteur.getListeArcs().get(key.getNomArc());

			//Si notre routeur est le routeur A alors le voisin est B
			if ( myRouteur.getNomRouteur() == current.getRouteurA().getNomRouteur() )
			{
				this.ArcToVoisins.put(current.getRouteurB().getNomRouteur(), current);
				this.bestPath.put(current.getRouteurB().getPort(), current.getRouteurB());
				this.coutRouteur.put(current.getRouteurB(),current.getCout());
				logger.info("Routeur"+ myRouteur.getNomRouteur() + ": Neighbor " + current.getRouteurB().getNomRouteur()+ " added to table");
			}

			//Si notre routeur est le routeur B alors le voisin est A
			else if (myRouteur.getNomRouteur() == current.getRouteurB().getNomRouteur()  )
			{
				this.ArcToVoisins.put(current.getRouteurA().getNomRouteur(), current);
				this.bestPath.put(current.getRouteurA().getPort(), current.getRouteurA());
				this.coutRouteur.put(current.getRouteurA(),current.getCout());
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
				
				//Tranlater la table de routage en table d'elements serialisable
				remplirToSend(this.bestPath);
				
				
				// On construit le paquet contenant la table a envoyer
				construirePacket(toSend);
				
				//On envoie le packet avec la table
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
				Enumeration<Routeur>testBP = bestPath.elements();
				while(testBP.hasMoreElements())
				{
					Routeur test=testBP.nextElement();
					logger.info("Routeur ISSSSS"  + myRouteur.getNomRouteur() + " : Neighbor is " + test.getNomRouteur());
				}
				
				/*logger.info("Routeur"+myRouteur.getNomRouteur()+"Current New.getName()" +currentNew.getNomRouteur()+"  : CurrentNew.getPort " +currentNew.getPort()
				+" myRouteur.getPort()" + myRouteur.getPort()+" this.bestPath.containsKey" + this.bestPath.containsKey(key.getPort()));
				*/
				trouverVoisin();
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
			//yourBytes = bos.toByteArray();
			giftToNeighbor.setData(bos.toByteArray())  ;

		} catch (IOException ex) {
			System.out.println("Routeur-" + myRouteur.getNomRouteur()+"" + ex.getMessage());
		}
		//yourBytes=Marshallizer.marshallize(Object);
	}

	private void sendPacket(UDPPacket udpPacket, int destinationPort) {
		try {
			logger.info("Routeur-" + myRouteur.getNomRouteur() + ": sendPacket executed");
			logger.info("Routeur-" + myRouteur.getNomRouteur() + ": sendPacket : " + udpPacket.toString());
			byte[] packetData = Marshallizer.marshallize(udpPacket);
			DatagramPacket datagram = new DatagramPacket(packetData,packetData.length, InetAddress.getLocalHost(), destinationPort); // port de la passerelle par default
			myRouteur.getRouteurSocket().send(datagram); // émission non-bloquante
		} catch (SocketException e) {
			System.out.println("Routeur-" + myRouteur.getNomRouteur() + " Socket: " + e.getMessage());
		} catch (IOException e) {
			//System.out.println("Routeur-" + myRouteur.getNomRouteur() + " IO: " + e.getMessage());
		}


	}

	private void remplirToSend(Hashtable<Integer,Routeur> tableToTranslate)
	{
		Enumeration<Routeur> num = tableToTranslate.elements();
		Enumeration <Integer> keynum = tableToTranslate.keys();

		while(num.hasMoreElements() && keynum.hasMoreElements())
		{
			
			Routeur key = num.nextElement();
			Integer value = keynum.nextElement();
			
			Routeur currentElement = (Routeur)tableToTranslate.get(key.getPort());
			
			toSend.put(value, currentElement.getNomRouteur());
		}
	}
}