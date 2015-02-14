package com.pronofoot.teamazerty.app.model;

import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by PBVZ9205 on 11/09/2014.
 */
public class GrilleNavigator {

    public GrilleNavigator() {}

    /**
     * Quel est la précédante grille
     * @param grilles
     * @param grille_id
     * @return
     */
    public Grille findPrevGrille(ArrayList<Grille> grilles, int grille_id) {
        Grille res = null;
        for (Grille g : grilles) {
            if (g.getGrille_id() == grille_id) {
                return res;
            }
            res = g;
        }
        return res;
    }

    /**
     * Quel est la grille suivante
     * @param grilles
     * @param grille_id
     * @return
     */
    public Grille findNextGrille(ArrayList<Grille> grilles, int grille_id) {
        boolean trouve = false;
        for (Grille g : grilles) {
            if (trouve) {
                return g;
            }
            if (g.getGrille_id() == grille_id) {
                trouve = true;
            }
        }
        return null;
    }

    /**
     * A partir de la structure récupérée, on va classer les grilles par date
     * @param selector
     * @return
     */
    public ArrayList<Grille> sortSelector(CompetitionSelector selector) {
        ArrayList<Grille> res = new ArrayList<Grille>();
        for (Competition competition : selector.getCompetitions()) {
            for (Grille grille : competition.getGrilles()) {
                res.add(grille);
            }
        }
        Collections.sort(res, new ComparateurGrille());
        return res;
    }
}
