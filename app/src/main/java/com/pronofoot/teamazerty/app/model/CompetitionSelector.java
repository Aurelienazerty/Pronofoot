package com.pronofoot.teamazerty.app.model;

import java.util.List;

/**
 * Created by PBVZ9205 on 21/02/14.
 */
public class CompetitionSelector {

    protected List<Competition> competitions;
    protected String nom;

    public CompetitionSelector(final String nom, final List<Competition> competitions) {
        this.nom = nom;
        this.competitions = competitions;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(final List<Competition> competitions) {
        this.competitions = competitions;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(final String nom) {
        this.nom = nom;
    }

    public String toString() {
        String res = "";
        for (Competition competition : this.getCompetitions()) {
            for (Grille grille : competition.getGrilles()) {
                res += grille.getGrille_id() + ",";
            }
        }
        return res;
    }
}
