package com.pronofoot.teamazerty.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.model.Match;

import java.text.SimpleDateFormat;
import java.util.List;

public class EspacepronoResultatListAdapter extends AlternatingColorListAdapter<Match> {

    private Context context;

    /**
     * @param inflater
     * @param items
     */
    public EspacepronoResultatListAdapter(LayoutInflater inflater, List<Match> items) {
        super(R.layout.espaceprono_resultat_list_item, inflater, items, false);
        context = inflater.getContext();
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.grille_resultat_equipe_domicile,
                R.id.grille_resultat_domicile,
                R.id.grille_resultat_null,
                R.id.grille_resultat_exterieur,
                R.id.grille_resultat_equipe_exterieur,
                R.id.grille_resultat_date_match
        };
    }

    @Override
    protected void update(int position, Match item) {
        super.update(position, item);
        //Les noms d'équipes
        setText(0, item.getEquipeLocale().getNom());
        setText(4, item.getEquipeVisiteur().getNom());
        //Les résultats
        if (item.getVainqueur() == item.getEquipeLocale().getEquipe_id()) {
                setChecked(1, true);
        } else if (item.getVainqueur() ==0) {
                setChecked(2, true);
        } else if (item.getVainqueur() == item.getEquipeVisiteur().getEquipe_id()) {
                setChecked(3, true);
        }
        SimpleDateFormat formater = new SimpleDateFormat(context.getResources().getString(R.string.format_date));
        setText(5, formater.format(item.getDate()));
        //Log.i("TA", item.getEquipeLocale().getNom() + " VS " + item.getEquipeVisiteur().getNom() + " : " + item.isMatchNuls());

        this.view(1).setClickable(false);
        this.view(2).setClickable(false);
        this.view(3).setClickable(false);

        //Init
        this.view(1).setBackgroundResource(0);
        this.view(2).setBackgroundResource(0);
        this.view(3).setBackgroundResource(0);

        if (!item.isMatchNuls()) {
            this.view(2).setVisibility(View.INVISIBLE);
        } else {
            this.view(2).setVisibility(View.VISIBLE);
        }

        //Flag pour son prono
        if (item.getVainqueur() != item.getProno()) {
            //Petite croix pour dire qu'on a fait du caca
            if (item.getProno() == item.getEquipeLocale().getEquipe_id()) {
                this.view(1).setBackgroundResource(R.drawable.abs__ic_clear_search_api_holo_light);
            } else if (item.getProno() == 0) {
                this.view(2).setBackgroundResource(R.drawable.abs__ic_clear_search_api_holo_light);
            } else if (item.getProno() == item.getEquipeVisiteur().getEquipe_id()) {
                this.view(3).setBackgroundResource(R.drawable.abs__ic_clear_search_api_holo_light);
            }
        } else {
            //On met un petit check à la place et on désélection sinon on y voit rien
            if (item.getProno() == item.getEquipeLocale().getEquipe_id()) {
                setChecked(1, false);
                this.view(1).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            } else if (item.getProno() ==0) {
                setChecked(2, false);
                this.view(2).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            } else if (item.getProno() == item.getEquipeVisiteur().getEquipe_id()) {
                setChecked(3, false);
                this.view(3).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            }
        }
    }
}
