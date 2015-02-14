package com.pronofoot.teamazerty.app.model;

import java.io.Serializable;

/**
 * Created by PBVZ9205 on 05/06/2014.
 */
public class Stat implements Serializable {

    private static final long serialVersionUID = -271019810007L;

    private String nom;
    private int classement_pt;
    private int nbPoint;
    private int compet_id;
    private int classement_pc;
    private float pourcent;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getClassement_pt() {
        return classement_pt;
    }

    public void setClassement_pt(int classement_pt) {
        this.classement_pt = classement_pt;
    }

    public int getNbPoint() {
        return nbPoint;
    }

    public void setNbPoint(int nbPoint) {
        this.nbPoint = nbPoint;
    }

    public int getClassement_pc() {
        return classement_pc;
    }

    public void setClassement_pc(int classement_pc) {
        this.classement_pc = classement_pc;
    }

    public float getPourcent() {
        return pourcent;
    }

    public void setPourcent(float pourcent) {
        this.pourcent = pourcent;
    }

    public int getCompet_id() {
        return compet_id;
    }

    public void setCompet_id(int compet_id) {
        this.compet_id = compet_id;
    }
}
