package com.pronofoot.teamazerty.app.model;

import java.util.Comparator;

/**
 * Created by PBVZ9205 on 11/09/2014.
 */
public class ComparateurGrille implements Comparator<Grille> {

    public int compare(Grille g1, Grille g2) {
       return g1.CompareTo(g2);
    }
}
