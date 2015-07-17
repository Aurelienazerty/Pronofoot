package com.pronofoot.teamazerty.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.Match;

import java.util.Collections;
import java.util.List;

public class EspacepronoResultatListFragment extends AsbstactGrilleResultatListFragment<Match> {

    private int lastResultat = -1;
    private int firstResultat = -1;

    @Override
    protected void configureList(final Activity activity, ListView listView) {
        super.configureList(activity, listView);
        lastResultat = -1;
        firstResultat = -1;

        header = activity.getLayoutInflater()
                .inflate(R.layout.espaceprono_resultat_entete, null);

        getListAdapter()
                .addHeader(header);

        footer = getActivity().getLayoutInflater()
                .inflate(R.layout.espaceprono_resultat_footer, null);
        getListAdapter()
                .addFooter(footer);
    }

    public void actualiserAffichage() {
        if (nomGrille != null) {
            String usernameAffiche;
            String usernameIndent = getActivity().getIntent().getStringExtra(Constants.Indent.USERNAME);
            if (usernameIndent != null) {
                usernameAffiche = usernameIndent;
            } else {
                usernameAffiche = username;
            }
            ((TextView) header.findViewById(R.id.grille_username)).setText(usernameAffiche);
            ((TextView) header.findViewById(R.id.grille_nom_grille)).setText(nomGrille);
            ((TextView) header.findViewById(R.id.grille_id_grille)).setText(id_grille + "");
            if (id_grille == -1 || lastResultat == -1 || id_grille == lastResultat) {
                header.findViewById(R.id.buttonPlusResultat).setVisibility(View.INVISIBLE);
            } else {
                header.findViewById(R.id.buttonPlusResultat).setVisibility(View.VISIBLE);
            }
            if (firstResultat == -1 || id_grille == firstResultat) {
                header.findViewById(R.id.buttonMoinsResultat).setVisibility(View.INVISIBLE);
            } else {
                header.findViewById(R.id.buttonMoinsResultat).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Loader<List<Match>> onCreateLoader(final int id, final Bundle args) {
        final List<Match> initialItems = items;
        return new ThrowableLoader<List<Match>>(getActivity(), items) {

            @Override
            public void deliverResult(List<Match> data) {
                super.deliverResult(data);
                if (data != null && data.size() > 0) {
                    actualiserAffichage();
                }
            }

            @Override
            public List<Match> loadData() throws Exception {
                try {
                    Activity a = getActivity();
                    if (a != null) {
                        Intent i = a.getIntent();
                        int grille_id = i.getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1);
                        int compet_id = i.getIntExtra(Constants.Indent.COMPET_ID, -1);
                        //Dans le cas où on passe un user_id particulier
                        int user = i.getIntExtra(Constants.Indent.USER_ID, -1);
                        if (user != -1) {
                            user_id = "" + user;
                        }

                        response = serviceProvider.getService(a).getGrille(user_id, username, password, grille_id, compet_id, regId, version);
                        nomGrille = response.getNom();
                        id_grille = response.getGrille_id();
                        lastResultat = a.getIntent().getIntExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1);
                        firstResultat = a.getIntent().getIntExtra(Constants.Indent.FIRST_GRILLE_RESULTAT, -1);
                        if (response != null && response.getMatchs() != null && response.getMatchs().size() > 0) {
                            return response.getMatchs();
                        }
                        return Collections.emptyList();
                    } else {
                        return Collections.emptyList();
                    }

                /*} catch (OperationCanceledException e) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }*/
                } catch (Exception e) {
                    Log.e("TA", e.getMessage());
                    e.printStackTrace();
                    return initialItems;
                }
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Match> createAdapter(List<Match> items) {
        return new EspacepronoResultatListAdapter(getActivity().getLayoutInflater(), items);
    }
}
