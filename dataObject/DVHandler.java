package dataObject;


import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public class DVHandler implements Runnable {

	private Hashtable <Integer,Routeur> neighborTable;
	private Routeur myRouteur;
	private String nomEnvoyeur;

	private static final Logger logger = Logger.getLogger(Routeur.class);


	public DVHandler(Hashtable<Integer, Routeur> receivedNeighborTable, Routeur currentRouteur)
	{
		// TODO Auto-generated constructor stub
		this.neighborTable = receivedNeighborTable;
		this.myRouteur = currentRouteur;
	}

	public void construireTable(Hashtable<Integer, Routeur> tableRecue, String nomEnvoyeur)
	{
		//Voisin ayant envoye sa table
		Arc currentVoisinArc = myRouteur.getArcToVoisins().get(nomEnvoyeur);
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

			if ( currentNew.getPort() != myRouteur.getPort() && myRouteur.getBestPath().containsKey(key) ==false)
			{
				myRouteur.getBestPath().put(currentNew.getPort(), currentNew);
				logger.info("Routeur: Chemin non existant. Chemin ajoute vers " + currentNew.getNomRouteur());
			}

			else if (currentNew.getPort() != myRouteur.getPort() && myRouteur.getBestPath().containsKey(key) ==true )
			{
				if( currentVoisinArc.getCout() + currentVoisin.getCoutRouteur().get(currentNew)  < myRouteur.getCoutRouteur().get(currentNew))
				{
					myRouteur.getBestPath().put(currentNew.getPort(), currentVoisin);
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

	private void start() {
		// TODO Auto-generated method stub
		try
		{
			nomEnvoyeur = myRouteur.getBestPath().get(myRouteur.getPortVoisin()).getNomRouteur();
			construireTable(this.neighborTable,this.nomEnvoyeur);
		}
		catch (Exception e) 
		{
			System.out.println("IO: " + e.getMessage());
		}

	}
}
