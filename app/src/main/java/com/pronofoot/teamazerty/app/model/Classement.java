package com.pronofoot.teamazerty.app.model;

import android.text.Html;

import java.io.Serializable;
import java.util.List;

/**
 * Created by PBVZ9205 on 24/01/14.
 */
public class Classement implements Serializable {

    private static final long serialVersionUID = -271019810001L;

    protected String nom;
    public List<Resultat> resultats;
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

    public List<Resultat> getResultats() {
        return resultats;
    }

    public void setResultats(final List<Resultat> resultats) {
        this.resultats = resultats;
    }

}
