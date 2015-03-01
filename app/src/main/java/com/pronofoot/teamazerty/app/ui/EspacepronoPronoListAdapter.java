package com.pronofoot.teamazerty.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.model.Match;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EspacepronoPronoListAdapter extends AlternatingColorListAdapter<Match> {

    private Context context;

    /**
     * @param inflater
     * @param items
     */
    public EspacepronoPronoListAdapter(LayoutInflater inflater, List<Match> items) {
        super(R.layout.espaceprono_prono_list_item, inflater, items, false);
        context = inflater.getContext();
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.grille_equipe_domicile,
                R.id.grille_prono_domicile,
                R.id.grille_prono_null,
                R.id.grille_prono_exterieur,
                R.id.grille_equipe_exterieur,
                R.id.grille_id_match,
                R.id.grille_id_equipe_locale,
                R.id.grille_id_equipe_exterieure,
                R.id.grille_date_match,
                R.id.grille_prono
        };
    }

    @Override
    protected void update(int position, Match item) {
        super.update(position, item);
        Date maintenant = new Date();
        //Les noms d'équipes
        setText(0, item.getEquipeLocale().getNom());
        setText(4, item.getEquipeVisiteur().getNom());
        //Les id cachés
        setText(5, item.getMatch_id() + "");
        setText(6, item.getEquipeLocale().getEquipe_id() + "");
        setText(7, item.getEquipeVisiteur().getEquipe_id() + "");
        //Les résultats
        RadioGroup rg = this.view(9);
        rg.clearCheck();
        if (!item.isMatchNuls()) {
            this.view(2).setVisibility(View.INVISIBLE);
        } else {
            this.view(2).setVisibility(View.VISIBLE);
        }

        //Init
        this.view(1).setBackgroundResource(0);
        this.view(2).setBackgroundResource(0);
        this.view(3).setBackgroundResource(0);

        //Désactivation lorsque le match a commencé
        if (item.getDate().before(maintenant)) {
            this.view(1).setClickable(false);
            this.view(2).setClickable(false);
            this.view(3).setClickable(false);
        } else {
            this.view(1).setClickable(true);
            this.view(2).setClickable(true);
            this.view(3).setClickable(true);
        }

        if (item.getVainqueur() == item.getEquipeLocale().getEquipe_id()) {
            if (item.getDate().before(maintenant)) {
                this.view(1).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            } else {
                rg.check(R.id.grille_prono_domicile);
            }
        } else if (item.getVainqueur() == item.getEquipeVisiteur().getEquipe_id()) {
            if (item.getDate().before(maintenant)) {
                this.view(3).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            } else {
                rg.check(R.id.grille_prono_exterieur);
            }
        } else if (item.getVainqueur() == 0) {
            if (item.getDate().before(maintenant)) {
                this.view(2).setBackgroundResource(R.drawable.abs__ic_cab_done_holo_light);
            } else {
                rg.check(R.id.grille_prono_null);
            }
        }

        SimpleDateFormat formater = new SimpleDateFormat(context.getResources().getString(R.string.format_date));
        setText(8, formater.format(item.getDate()));
    }
}
