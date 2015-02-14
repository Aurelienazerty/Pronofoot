package com.pronofoot.teamazerty.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by PBVZ9205 on 23/01/14.
 */
public class Match implements Serializable {

    private static final long serialVersionUID = -271019810005L;

    protected Equipe equipeLocale;
    protected Equipe equipeVisiteur;
    protected Date date;
    protected int vainqueur;
    protected int prono;
    protected int match_id;
    protected int grille_id;
    protected boolean matchNuls;

    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }


    public Equipe getEquipeLocale() {
        return equipeLocale;
    }

    public void setEquipeLocale(final Equipe equipeLocale) {
        this.equipeLocale = equipeLocale;
    }

    public Equipe getEquipeVisiteur() {
        return equipeVisiteur;
    }

    public void setEquipeVisiteur(final Equipe equipeVisiteur) {
        this.equipeVisiteur = equipeVisiteur;
    }

    public int getVainqueur() {
        return vainqueur;
    }

    public void setVainqueur(final int vainqueur) {
        this.vainqueur = vainqueur;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(final int match_id) {
        this.match_id = match_id;
    }

    public int getGrille_id() {
        return grille_id;
    }

    public void setGrille_id(final int grille_id) {
        this.grille_id = grille_id;
    }

    public boolean isMatchNuls() {
        return matchNuls;
    }

    public void setMatchNuls(final boolean matchNuls) {
        this.matchNuls = matchNuls;
    }

    public String toString() {
        return "{" +
        "match_id:" + match_id + "," +
        "grille_id:" + grille_id + "," +
        "date:" + date + "," +
        "vainqueur:" + vainqueur + "," +
        "equipeLocale:" + equipeLocale + "," +
        "equipeVisiteur:" + equipeVisiteur +
        "}";
    }

    public int getProno() {
        return prono;
    }

    public void setProno(final int prono) {
        this.prono = prono;
    }
}
