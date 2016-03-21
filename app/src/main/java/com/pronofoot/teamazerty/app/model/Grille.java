package com.pronofoot.teamazerty.app.model;

import android.text.Html;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by PBVZ9205 on 23/01/14.
 */
public class Grille implements Serializable {

    private static final long serialVersionUID = -271019810004L;

    public List<Match> matchs;
    protected int grille_id;
    protected String nom;
    protected boolean matchNuls;
    protected boolean prolongations;
    protected Date debut;
    protected Date fin;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public List<Match> getMatchs() {
        return matchs;
    }

    public void setMatchs(final List<Match> matchs) {
        this.matchs = matchs;
    }

    public String getNom() {
        return Html.fromHtml(nom).toString();
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }


    public boolean isProlongations() {
        return prolongations;
    }

    public void setProlongations(final boolean prolongations) {
        this.prolongations = prolongations;
    }

    public boolean isMatchNuls() {
        return matchNuls;
    }

    public void setMatchNuls(final boolean matchNuls) {
        this.matchNuls = matchNuls;
    }

    public int getGrille_id() {
        return grille_id;
    }

    public void setGrille_id(final int grille_id) {
        this.grille_id = grille_id;
    }

    public int CompareTo(final Grille grille) {
        int resultat = 0;
        if (this.debut.before(grille.debut)) {
            resultat = -1;
        } else if (this.debut.after(grille.debut)) {
            resultat = 1;
        } else {
            resultat = 0;
        }
        return resultat;
    }

    public String toString() {
        return "{" +
        "class:" + "Grille," +
        "grille_id:" + grille_id + "," +
        "nom:" + getNom() + "," +
        "Match Null:" + isMatchNuls() + "," +
        "Prolongation:" + isProlongations() + "," +
        "matchs:" + matchs + "," +
        "}";
    }

    public Date getDebut() {
        return debut;
    }

    public void setDebut(final Date debut) {
        this.debut = debut;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(final Date fin) {
        this.fin = fin;
    }
}
