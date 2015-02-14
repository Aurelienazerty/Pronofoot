package com.pronofoot.teamazerty.app.model;

import java.io.Serializable;

/**
 * Created by PBVZ9205 on 24/01/14.
 */
public class Resultat implements Serializable {
    protected int classement;
    public int varation;
    protected Pronostiqueur pronostiqueur;
    protected String valeur;
    protected String unite;
    protected String nbPoint;
    protected String pourcent;

    private static final long serialVersionUID = -271019810007L;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }


    public String getClassement() {
        return classement + "";
    }

    public void setClassement(final int classement) {
        this.classement = classement;
    }

    public Pronostiqueur getPronostiqueur() {
        return pronostiqueur;
    }

    public void setPronostiqueur(Pronostiqueur pronostiqueur) {
        this.pronostiqueur = pronostiqueur;
    }

    public String getVaration() {
        if (varation == 0) {
            return "=";
        }
        return "" + varation;
    }

    public void setVaration(final int varation) {
        this.varation = varation;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(final String unite) {
        this.unite = unite;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(final String valeur) {
        this.valeur = valeur;
    }

    public String getNbPoint() {
        return nbPoint;
    }

    public void setNbPoint(final String nbPoint) {
        this.nbPoint = nbPoint;
    }

    public String getPourcent() {
        return pourcent;
    }

    public void setPourcent(final String pourcent) {
        this.pourcent = pourcent;
    }
}
