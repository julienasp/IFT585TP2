/**
 *
 * @author JUASP-G73-Android
 */
package dataObject;
public class Arc {
    /**************************************/
    /********* PRIVATE ATTRIBUTS **********/
    /**************************************/
    private String nomArc;
    private Routeur routeurA;
    private Routeur routeurB;
    private int cout;

    
    /**************************************/
    /************ CONSTRUCTOR *************/
    /**************************************/
    public Arc(String nomArc, Routeur routeurA, Routeur routeurB, int cout) {
        this.nomArc = nomArc;
        this.routeurA = routeurA;
        this.routeurB = routeurB;
        this.cout = cout;
    }

    
    /**************************************/
    /********* GETTER AND SETTER **********/
    /**************************************/
    public Routeur getRouteurA() {
        return routeurA;
    }

    public void setRouteurA(Routeur routeurA) {
        this.routeurA = routeurA;
    }

    public Routeur getRouteurB() {
        return routeurB;
    }

    public void setRouteurB(Routeur routeurB) {
        this.routeurB = routeurB;
    }

    public int getCout() {
        return cout;
    }

    public void setCout(int cout) {
        this.cout = cout;
    }   

    public String getNomArc() {
        return nomArc;
    }

    public void setNomArc(String nomArc) {
        this.nomArc = nomArc;
    }    
    
}
