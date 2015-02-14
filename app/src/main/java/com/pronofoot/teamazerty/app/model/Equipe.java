package com.pronofoot.teamazerty.app.model;

import android.text.Html;

import java.io.Serializable;

/**
 * Created by PBVZ9205 on 23/01/14.
 */
public class Equipe implements Serializable {

    private static final long serialVersionUID = -271019810003L;

    protected String nom;
    protected int equipe_id;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public String getNom() {
        return Html.fromHtml(nom).toString();
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }


    public String toString() {
        return "{" +
        "equipe_id:" + getEquipe_id() + "," +
        "nom:" + getNom() +
        "}";
    }

    public int getEquipe_id() {
        return equipe_id;
    }

    public void setEquipe_id(final int equipe_id) {
        this.equipe_id = equipe_id;
    }
}
