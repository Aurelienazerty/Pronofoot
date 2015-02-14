package com.pronofoot.teamazerty.app.model;

import android.text.Html;

import java.io.Serializable;

/**
 * Created by PBVZ9205 on 23/01/14.
 */
public class Pronostiqueur implements Serializable {

    private static final long serialVersionUID = -271019810006L;

    protected String nom;
    protected int id;

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

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
