package com.pronofoot.teamazerty.app.ui;

import android.view.LayoutInflater;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.model.Resultat;

import java.util.List;

public class ResultatGrilleListAdapter extends AlternatingColorListAdapter<Resultat> {

    String unitePoint, unitePourcent;
    private final LayoutInflater inflater;

    /**
     * @param inflater
     * @param items
     */
    public ResultatGrilleListAdapter(LayoutInflater inflater, List<Resultat> items) {
        super(R.layout.resultat_grille_list_item, inflater, items, true);
        unitePoint = inflater.getContext().getResources().getString(R.string.unite_point);
        unitePourcent = inflater.getContext().getResources().getString(R.string.unite_pourcent);
        this.inflater = inflater;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.resultat_classement,
                R.id.resultat_username,
                R.id.resultat_point,
                R.id.resultat_pourcent
        };
    }

    @Override
    protected void update(int position, Resultat item) {
        super.update(position, item);

        setText(0, item.getClassement() + ".");
        setText(1, item.getPronostiqueur().getNom());
        setText(2, item.getNbPoint() + " " + unitePoint);
        setText(3, item.getPourcent() + " " + unitePourcent);
        //setNumber(R.id.tv_date, item.getCreatedAt());
    }
}
