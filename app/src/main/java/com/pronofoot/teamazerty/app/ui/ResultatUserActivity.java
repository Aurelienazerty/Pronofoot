package com.pronofoot.teamazerty.app.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.Window;
import com.github.kevinsawicki.wishlist.Toaster;
import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.CompetitionSelector;
import com.pronofoot.teamazerty.app.model.Grille;
import com.pronofoot.teamazerty.app.model.GrilleNavigator;
import com.pronofoot.teamazerty.app.model.Match;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by PBVZ9205 on 11/09/2014.
 */
public class ResultatUserActivity extends PronofootFragmentActivity {

    @InjectView(R.id.epi_header)
    TitlePageIndicator indicator;

    @Inject BootstrapServiceProvider serviceProvider;
    private SafeAsyncTask<Boolean> grilleTask;
    public ArrayList<Grille> grilleResultat;
    private ArrayList<AsbstactGrilleResultatListFragment<Match>> fragments;
    private GrilleNavigator grilleNavigator = new GrilleNavigator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resuser_view);
        Injector.inject(this);

        chargerListeGrille();
        grilleNavigator = new GrilleNavigator();
        //fragments = new ArrayList<AsbstactGrilleResultatListFragment<Match>>();

    }

    private void chargerListeGrille() {
        grilleResultat = new ArrayList<Grille>();
        final SharedPreferences preferences = EspacepronoPronoListFragment.getSharedPreferences(this);

        grilleTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Boolean res = false;
                //this.
                try {
                    int compet_id = getIntent().getIntExtra(Constants.Indent.COMPET_ID, -1);
                    CompetitionSelector listeResultat = serviceProvider.getService(ResultatUserActivity.this).getListCompetition(false, compet_id);
                    grilleResultat = grilleNavigator.sortSelector(listeResultat);

                    if (!grilleResultat.isEmpty()) {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_RESULTAT, grilleResultat.get(0).getGrille_id());
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, grilleResultat.get(grilleResultat.size() - 1).getGrille_id());
                    } else {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_RESULTAT, -1);
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1);
                    }

                    res = true;
                } catch (IOException e) {
                    Toaster.showLong(ResultatUserActivity.this, R.string.error_loading_list_grille);
                }

                return res;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Throwable cause = e.getCause() != null ? e.getCause() : e;

                String message;
                // A 404 is returned as an Exception with this message
                if ("Received authentication challenge is null".equals(cause
                        .getMessage()))
                    message = getResources().getString(
                            R.string.message_bad_credentials);
                else
                    message = cause.getMessage();

                Toaster.showLong(ResultatUserActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {}

            @Override
            protected void onFinally() throws RuntimeException {
                //hideProgress();
            }
        };
        grilleTask.execute();
    }

    public void addFragment(AsbstactGrilleResultatListFragment f) {
        if (fragments == null) {
            fragments = new ArrayList<AsbstactGrilleResultatListFragment<Match>>();
        }
        fragments.add(f);
        //f.forceRefresh();
    }

    private void restartActivity() {
        //Sauvegarde du contexte
        getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1));
        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1));
        getIntent().putExtra(Constants.Indent.COMPET_ID, getIntent().getIntExtra(Constants.Indent.COMPET_ID, -1));
        for (AsbstactGrilleResultatListFragment f : fragments) {
            f.forceRefresh();
        }
    }

    public void nextGrilleResultat(View button) {
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1);
        if (grille_id == -1 && grilleResultat.size() > 0) {
            grille_id = grilleResultat.get(grilleResultat.size() - 1).getGrille_id();
        }
        Grille next = grilleNavigator.findNextGrille(grilleResultat, grille_id);

        if (next != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, next.getGrille_id());
        }
        restartActivity();
    }

    public void previousGrilleResultat(View button) {
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1);
        if (grille_id == -1 && grilleResultat.size() > 0) {
            grille_id = grilleResultat.get(grilleResultat.size() - 1).getGrille_id();
        }
        Grille prev = grilleNavigator.findPrevGrille(grilleResultat, grille_id);

        if (prev != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, prev.getGrille_id());
        }
        restartActivity();
    }


}
