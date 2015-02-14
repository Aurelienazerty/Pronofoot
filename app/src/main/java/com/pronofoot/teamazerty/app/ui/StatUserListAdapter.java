package com.pronofoot.teamazerty.app.ui;

import android.view.LayoutInflater;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.model.Stat;

import java.util.List;

public class StatUserListAdapter extends AlternatingColorListAdapter<Stat> {

    String unitePoint, unitePourcent, uniteClassement, noRank;

    /**
     * @param inflater
     * @param items
     */
    public StatUserListAdapter(LayoutInflater inflater, List<Stat> items) {
        super(R.layout.statuser_list_item, inflater, items);
        unitePoint = inflater.getContext().getResources().getString(R.string.unite_point);
        unitePourcent = inflater.getContext().getResources().getString(R.string.unite_pourcent);
        uniteClassement = inflater.getContext().getResources().getString(R.string.unite_classement);
        noRank = inflater.getContext().getResources().getString(R.string.no_partcipation);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.stat_classement_point,
                R.id.stat_point,
                R.id.stat_classement_pourcent,
                R.id.stat_pourcent,
                R.id.stat_competition
        };
    }

    @Override
    protected void update(int position, Stat item) {
        super.update(position, item);

        if (item.getClassement_pt() != -1) {
            setText(0, item.getClassement_pt() + uniteClassement);
            setText(1, item.getNbPoint() + " "  + unitePoint);
        } else {
            setText(0, noRank);
            setText(1, "");
        }
        if (item.getClassement_pc() != -1) {
            setText(2, item.getClassement_pc() + uniteClassement);
            setText(3, item.getPourcent() + " " + unitePourcent);
        } else {
            setText(2, noRank);
            setText(3, "");
        }

        setText(4, item.getNom());

    }
}
