import dataObject.*;
import java.util.Scanner;


public class StartPoint {	
	
	public static void main(String[] args) {
            
                /**************************************************/
                /***********  CONFIGURATION DU RESEAU *************/
                /**************************************************/
                
                Reseau monReseau = new Reseau();
                
                //Initiation des hotes
                Hote h1 = new Hote("h1",8000,9000);                
                Hote h2 = new Hote("h2",8001,9006);
                
                //Initiation des routeurs
                Routeur rA = new Routeur("A", 9000);
                Routeur rB = new Routeur("B", 9001);
                Routeur rC = new Routeur("C", 9002);
                Routeur rD = new Routeur("D", 9003);
                Routeur rE = new Routeur("E", 9004);
                Routeur rF = new Routeur("F", 9005);
                
                //Initiation des arcs
                Arc ab = new Arc("ab",rA,rB,5);
                Arc ad = new Arc("ad",rA,rD,45);
                Arc bc = new Arc("bc",rB,rC,70);
                Arc be = new Arc("be",rB,rE,3);
                Arc cd = new Arc("cd",rC,rD,50);
                Arc cf = new Arc("cf",rC,rF,78);
                Arc de = new Arc("de",rD,rE,8);
                Arc ef = new Arc("ef",rE,rF,7);
                
                //Initialisation du réseau
                
                //Ajouts des hôtes
                monReseau.ajouterHote(h1);
                monReseau.ajouterHote(h2);
                
                //Ajouts des routeurs
                monReseau.ajouterRouteur(rA);
                monReseau.ajouterRouteur(rB);
                monReseau.ajouterRouteur(rC);
                monReseau.ajouterRouteur(rD);
                monReseau.ajouterRouteur(rE);
                monReseau.ajouterRouteur(rF);
                
                //Ajout sdes arcs
                monReseau.ajouterArc(ab);
                monReseau.ajouterArc(ad);
                monReseau.ajouterArc(bc);
                monReseau.ajouterArc(be);
                monReseau.ajouterArc(cd);
                monReseau.ajouterArc(cf);
                monReseau.ajouterArc(de);
                monReseau.ajouterArc(ef);
                
                /**************************************************/
                /******* FIN DE LA CONFIGURATION DU RESEAU  *******/
                /**************************************************/
                
                

		Scanner sc = new Scanner(System.in);
                
                System.out.println("Quels algorithme de routage souhaitez-vous utiliser ? ");
                System.out.println("1 --- LS (Link-state) --- ");
                System.out.println("2 --- DV (Distance-vector) --- ");
                System.out.println("0 --- Aucun --- ");
                
                int choixUser = sc.nextInt();
		
                switch (choixUser) 
                {
                case 0:

                        sc.close();
                        break;

                case 1:
                        monReseau.setTypeDeRoutage(Reseau.LSROUTING);
                        monReseau.start();                        
                        break;

                case 2: 
                        monReseau.setTypeDeRoutage(Reseau.DVROUTING);
                        monReseau.start();                        
                        break;
                }
		
                //Timer pour l'attente du routage
                h1.start(); // Pour la reception de message
                h2.start(); // Pour la reception de message
                h1.envoyerMessage("h1 to h2: Hello beautiful world!",h2.getPort());
                h2.envoyerMessage("h2 to h1: Hello beautiful Host!",h1.getPort());
		
	}

}
