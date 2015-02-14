package com.pronofoot.teamazerty.app.ui;

import android.graphics.Color;
import android.view.LayoutInflater;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.model.Resultat;

import java.util.List;

public class ResultatClassementListAdapter extends AlternatingColorListAdapter<Resultat> {

    private final LayoutInflater inflater;

    /**
     * @param inflater
     * @param items
     */
    public ResultatClassementListAdapter(LayoutInflater inflater, List<Resultat> items) {
        super(R.layout.resultat_classement_list_item, inflater, items, true);
        this.inflater = inflater;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.classement_classement,
                R.id.classement_variation,
                R.id.classement_username,
                R.id.classement_valeur
        };
    }

    @Override
    protected void update(int position, Resultat item) {
        super.update(position, item);

        String variation = item.getVaration();

        if (item.varation < 0) {
            textView(1).setTextColor(Color.parseColor("red"));
        } else  if (item.varation > 0) {
            textView(1).setTextColor(Color.parseColor("#007F0E"));
            variation = "+" + item.varation;
        } else {
            String colorDef = inflater.getContext().getString(R.color.table_text);
            textView(1).setTextColor(Color.parseColor(colorDef));
        }

        setText(0, item.getClassement() + ".");
        setText(1, variation);
        setText(2, item.getPronostiqueur().getNom());
        setText(3, item.getValeur() + " " + item.getUnite());
    }
}
