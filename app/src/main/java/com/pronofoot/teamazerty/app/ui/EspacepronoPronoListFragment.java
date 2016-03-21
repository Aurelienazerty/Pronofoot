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

public class EspacepronoPronoListFragment extends AsbstactGrilleResultatListFragment<Match> {

    private int firstProno = -1;
    private int lastProno = -1;

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);
        firstProno = -1;
        lastProno = -1;
        header = activity.getLayoutInflater()
                .inflate(R.layout.espaceprono_prono_entete, null);
        getListAdapter()
                .addHeader(header);

        footer = activity.getLayoutInflater()
                .inflate(R.layout.espaceprono_prono_footer, null);
        getListAdapter()
                .addFooter(footer);
    }

    public void actualiserAffichage() {
        if (nomGrille != null) {
            ((TextView) header.findViewById(R.id.grille_nom_grille)).setText(nomGrille);
            String info;
            if (!prolongations) {
                info = getResources().getString(R.string.info_prolongation);
            } else if (!matchNull) {
                info = getResources().getString(R.string.info_no_null);
            } else {
                info = "";
            }
            ((TextView) header.findViewById(R.id.lbl_infomatch)).setText(info);
            ((TextView) header.findViewById(R.id.grille_id_grille)).setText(id_grille + "");
            if (id_grille == -1 || firstProno == -1 || id_grille == firstProno) {
                header.findViewById(R.id.buttonMoinsProno).setVisibility(View.INVISIBLE);
            } else {
                header.findViewById(R.id.buttonMoinsProno).setVisibility(View.VISIBLE);
            }
            if (lastProno == -1 || id_grille == lastProno) {
                header.findViewById(R.id.buttonPlusProno).setVisibility(View.INVISIBLE);
            } else {
                header.findViewById(R.id.buttonPlusProno).setVisibility(View.VISIBLE);
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
                actualiserAffichage();
            }

            @Override
            public List<Match> loadData() throws Exception {
                try {
                    Activity a = getActivity();
                    if (a != null) {
                        Intent i = a.getIntent();
                        int grille_id = i.getIntExtra(Constants.Indent.GRILLE_PRONO, -1);
                        int compet_id = i.getIntExtra(Constants.Indent.COMPET_ID, -1);
                        //this.getContext().getString();
                        response = serviceProvider.getService(getActivity()).getGrilleForUser(user_id, username, password, grille_id, compet_id, regId, version);
                        nomGrille = response.getNom();
                        id_grille = response.getGrille_id();
                        matchNull = response.isMatchNuls();
                        prolongations = response.isProlongations();

                        firstProno = a.getIntent().getIntExtra(Constants.Indent.FIRST_GRILLE_PRONO, -1);
                        lastProno = a.getIntent().getIntExtra(Constants.Indent.LAST_GRILLE_PRONO, -1);
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
        return new EspacepronoPronoListAdapter(getActivity().getLayoutInflater(), items);
    }
}
