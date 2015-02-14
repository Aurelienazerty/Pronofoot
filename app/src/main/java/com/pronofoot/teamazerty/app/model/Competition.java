package com.pronofoot.teamazerty.app.model;

import android.text.Html;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PBVZ9205 on 23/01/14.
 */
public class Competition implements Serializable {

    private static final long serialVersionUID = -271019810002L;

    protected ArrayList<Grille> grilles;
    protected String nom;
    protected int compet_id;

    protected String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public ArrayList<Grille> getGrilles() {
        return grilles;
    }

    public void setGrilles(final ArrayList<Grille> grilles) {
        this.grilles = grilles;
    }

    public String getNom() {
        return Html.fromHtml(nom).toString();
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }


    public String toString() {
        return "{" +
        "class:" + "Competition," +
        "compet_id:" + getCompet_id() + "," +
        "nom:" + getNom() + "," +
        "grilles:" + getGrilles() + "," +
        "}";
    }

    public int getCompet_id() {
        return compet_id;
    }

    public void setCompet_id(final int compet_id) {
        this.compet_id = compet_id;
    }
}
