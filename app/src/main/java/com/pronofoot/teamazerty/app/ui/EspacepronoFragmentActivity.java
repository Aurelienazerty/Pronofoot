package com.pronofoot.teamazerty.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.github.kevinsawicki.wishlist.Toaster;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.CompetitionSelector;
import com.pronofoot.teamazerty.app.model.Grille;
import com.pronofoot.teamazerty.app.model.GrilleNavigator;
import com.pronofoot.teamazerty.app.model.Match;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;

import net.simonvt.menudrawer.MenuDrawer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by PBVZ9205 on 12/02/14.
 */
public class EspacepronoFragmentActivity extends AbstractPronofootFragmentActivity {

    @InjectView(R.id.epi_header)
    TitlePageIndicator indicator;
    @InjectView(R.id.ep_pages)
    ViewPager pager;
    /*@InjectView(R.id.vp_ads)
    Fragment adBloc;*/

    ProgressDialog progress;


    public final static int PAGE_PRONO = 0;
    public final static int PAGE_RESULTAT = 1;

    private SafeAsyncTask<Boolean> pronosticTask, grilleTask;
    private static View button_pronostique;
    //Parce qu'on relance l'activité quand on change de grille
    //private Intent getIntent();
    private EspacepronoPagerAdapter espacepronoPagerAdapter;
    private GrilleNavigator grilleNavigator = new GrilleNavigator();

    public ArrayList<Grille> grilleProno, grilleResultat;
    private ArrayList<AsbstactGrilleResultatListFragment<Match>> fragments;
    private ArrayList<BasicNameValuePair> changedProno = new ArrayList<BasicNameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        // Set up navigation drawer
        menuDrawer = MenuDrawer.attach(this);
        menuDrawer.setMenuView(R.layout.navigation_drawer);
        menuDrawer.setContentView(R.layout.espaceprono_view);
        menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        menuDrawer.setDrawerIndicatorEnabled(true);

        Views.inject(this);

        checkAuth();
        //getIntent() = new Intent(this, EspacepronoActivity.class);
        chargerListeGrille();
        changedProno = new ArrayList<BasicNameValuePair>();

        //fragments = new ArrayList<AsbstactGrilleResultatListFragment<Match>>();
        boolean isLunched = getIntent().getBooleanExtra(Constants.Indent.LUNCHED, false);
        if (!isLunched) {
            SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
            boolean startOnResult = preferences.getString(Constants.Param.PARAM_HOME_PAGE_IS_RESULT, "n").equalsIgnoreCase("y");
            if (startOnResult) {
                navigateToResultat();
            }
        }
    }

    protected void initScreen() {
        if(userHasAuthenticated) {
            pager.removeAllViews();
            espacepronoPagerAdapter  = new EspacepronoPagerAdapter(getResources(), getSupportFragmentManager());
            pager.setAdapter(espacepronoPagerAdapter);

            indicator.setViewPager(pager);
            Intent i = getIntent();
            int page = i.getIntExtra(Constants.Indent.PAGE, PAGE_PRONO);
            pager.setCurrentItem(page);
        }
        setNavListeners();
    }

    protected void setNavListeners() {
        menuDrawer.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
                navigateToResultat();
            }
        });

        menuDrawer.findViewById(R.id.espace_prono).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                menuDrawer.toggleMenu();
                return true;
            case R.id.espace_prono:
                menuDrawer.toggleMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToResultat() {
        final Intent i = new Intent(this, CarouselActivity.class);
        i.putExtra(Constants.Indent.LUNCHED, true);
        startActivity(i);
    }

    public void onCheckedChanged(View button) {
        RadioGroup rg = (RadioGroup) button.getParent();
        rg.clearCheck();
        rg.check(button.getId());
        String prono;
        String id_match = ((TextView)rg.findViewById(R.id.grille_id_match)).getText().toString();
        switch (button.getId()) {
            case R.id.grille_prono_domicile:
                prono = ((TextView)rg.findViewById(R.id.grille_id_equipe_locale)).getText().toString();
                break;
            case R.id.grille_prono_exterieur:
                prono = ((TextView)rg.findViewById(R.id.grille_id_equipe_exterieure)).getText().toString();
                break;
            default:
                prono = "0";
                break;
        }

        if (changedProno == null) {
            changedProno = new ArrayList<BasicNameValuePair>();
        }
        //Regarder s'il n'y ai pas
        String match = "match[" + id_match + "]";
        for (final BasicNameValuePair p : changedProno) {
            if (p.getName().equalsIgnoreCase(match)) {
                changedProno.remove(p);
            }
        }
        changedProno.add(new BasicNameValuePair("match[" + id_match + "]", prono));
    }

    public void envoyerPronostique(View button) {
        if (pronosticTask != null) {
            //Log.i("TA","pronosticTask != null");
            return;
        }
        this.button_pronostique = button;

        //showProgress();

        pronosticTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                Boolean res = false;

                SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
                String user_id = preferences.getString(Constants.Pref.PREF_USER_ID, "-1");
                String username = preferences.getString(Constants.Pref.PREF_LOGIN, "-1");
                String password = preferences.getString(Constants.Pref.PREF_PASSWORD, "-1");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                final String user_lang = Locale.getDefault().getLanguage();
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USER_ID, user_id));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERMAIL, username));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));

                //Récupérer les valeurs et construire le POST
                String id_grille = ((TextView)button_pronostique.getRootView().findViewById(R.id.grille_id_grille)).getText().toString();
                nameValuePairs.add(new BasicNameValuePair("id_grille", id_grille));
                //Les pronos modifiés
                for (final BasicNameValuePair p : changedProno) {
                    nameValuePairs.add(p);
                }

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.Http.URL_DO_PRONO);
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String dataRes = EntityUtils.toString(entity);
                    //Log.i("TA: retour", dataRes);
                    JSONObject jsonResponse = new JSONObject(dataRes);
                    res = jsonResponse.getBoolean("ok");
                    if (res) {
                        JSONArray j = jsonResponse.getJSONArray("retour");
                        String retour = getResources().getString(R.string.msg_prono_save);
                        /*for (int i = 0; i < j.length(); i++) {
                            retour += "\n" + j.getJSONObject(i).getString("libelle");
                        }*/
                        Toaster.showShort(EspacepronoFragmentActivity.this, Html.fromHtml(retour).toString());
                    }
                } catch (IOException e) {
                    Toaster.showLong(EspacepronoFragmentActivity.this, R.string.error_sending_prono);
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
                            R.string.error_sending_prono);
                else
                    message = cause.getMessage();

                Toaster.showLong(EspacepronoFragmentActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean t) {
                hideProgress();
                //Log.i("TA", "onFinally");
                pronosticTask = null;
                restartActivity();
            }

        };
        showProgress();
        pronosticTask.execute();

    }

    public void addFragment(AsbstactGrilleResultatListFragment f) {
        if (fragments == null) {
            fragments = new ArrayList<AsbstactGrilleResultatListFragment<Match>>();
        }
        fragments.add(f);
        f.forceRefresh();
    }

    private void restartActivity() {
        //Sauvegarde du contexte
        getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1));
        getIntent().putExtra(Constants.Indent.GRILLE_PRONO, getIntent().getIntExtra(Constants.Indent.GRILLE_PRONO, -1));
        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_PRONO, getIntent().getIntExtra(Constants.Indent.FIRST_GRILLE_PRONO, -1));
        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1));
        getIntent().putExtra(Constants.Indent.PAGE, getIntent().getIntExtra(Constants.Indent.PAGE, PAGE_PRONO));
        espacepronoPagerAdapter.notifyDataSetChanged();
        for (AsbstactGrilleResultatListFragment f : fragments) {
            f.forceRefresh();
        }
        changedProno = new ArrayList<BasicNameValuePair>();
    }

    public void nextGrilleProno(View button) {
        getIntent().putExtra(Constants.Indent.PAGE, PAGE_PRONO);
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_PRONO, -1);
        if (grille_id == -1 && grilleProno.size() > 0) {
            grille_id = grilleProno.get(0).getGrille_id();
        }
        Grille next = grilleNavigator.findNextGrille(grilleProno, grille_id);
        if (next != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_PRONO, next.getGrille_id());
            //SAV de l'autre tab
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1));
        }
        restartActivity();
    }

    public void nextGrilleResultat(View button) {
        getIntent().putExtra(Constants.Indent.PAGE, PAGE_RESULTAT);
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1);
        if (grille_id == -1 && grilleResultat.size() > 0) {
            grille_id = grilleResultat.get(grilleResultat.size() - 1).getGrille_id();
        }
        Grille next = grilleNavigator.findNextGrille(grilleResultat, grille_id);

        if (next != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, next.getGrille_id());
            //SAV de l'autre tab
            getIntent().putExtra(Constants.Indent.GRILLE_PRONO, getIntent().getIntExtra(Constants.Indent.GRILLE_PRONO, -1));
        }
        restartActivity();
    }

    public void previousGrilleProno(View button) {
        getIntent().putExtra(Constants.Indent.PAGE, PAGE_PRONO);
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_PRONO, -1);
        if (grille_id == -1 && grilleProno.size() > 0) {
            grille_id = grilleProno.get(0).getGrille_id();
        }
        Grille prev = grilleNavigator.findPrevGrille(grilleProno, grille_id);

        if (prev != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_PRONO, prev.getGrille_id());
            //SAV de l'autre tab
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1));
        }
        restartActivity();
    }

    public void previousGrilleResultat(View button) {
        getIntent().putExtra(Constants.Indent.PAGE, PAGE_RESULTAT);
        int grille_id = getIntent().getIntExtra(Constants.Indent.GRILLE_RESULTAT, -1);
        if (grille_id == -1 && grilleResultat.size() > 0) {
            grille_id = grilleResultat.get(grilleResultat.size() - 1).getGrille_id();
        }
        Grille prev = grilleNavigator.findPrevGrille(grilleResultat, grille_id);

        if (prev != null) {
            getIntent().putExtra(Constants.Indent.GRILLE_RESULTAT, prev.getGrille_id());
            //SAV de l'autre tab
            getIntent().putExtra(Constants.Indent.GRILLE_PRONO, getIntent().getIntExtra(Constants.Indent.GRILLE_PRONO, -1));
        }
        restartActivity();
    }

    /**
     * Hide progress dialog
     */
    protected void hideProgress() {
        progress.dismiss();
    }

    /**
     * Show progress dialog
     */
    protected void showProgress() {
        progress = ProgressDialog.show(this, getResources().getString(R.string.wait),
                getResources().getString(R.string.msg_sending_prono), true);
    }

    private void chargerListeGrille() {
        grilleProno = new ArrayList<Grille>();
        grilleResultat = new ArrayList<Grille>();
        final SharedPreferences preferences = EspacepronoPronoListFragment.getSharedPreferences(this);

        grilleTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Boolean res = false;
                try {
                    int compet_id = getIntent().getIntExtra(Constants.Indent.COMPET_ID, -1);
                    CompetitionSelector listeProno = serviceProvider.getService(EspacepronoFragmentActivity.this).getListCompetition(true, compet_id);
                    grilleProno = grilleNavigator.sortSelector(listeProno);
                    CompetitionSelector listeResultat = serviceProvider.getService(EspacepronoFragmentActivity.this).getListCompetition(false, compet_id);
                    grilleResultat = grilleNavigator.sortSelector(listeResultat);

                    if (!grilleResultat.isEmpty()) {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_RESULTAT, grilleResultat.get(0).getGrille_id());
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, grilleResultat.get(grilleResultat.size() - 1).getGrille_id());
                    } else {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_RESULTAT, -1);
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1);
                    }
                    if (!grilleProno.isEmpty()) {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_PRONO, grilleProno.get(0).getGrille_id());
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_PRONO, grilleProno.get(grilleProno.size() - 1).getGrille_id());
                    } else {
                        getIntent().putExtra(Constants.Indent.FIRST_GRILLE_PRONO, -1);
                        getIntent().putExtra(Constants.Indent.LAST_GRILLE_PRONO, -1);
                    }

                    res = true;
                } catch (IOException e) {
                    //Log.i("TA", "Erreur chargerListeGrille : " + e.getMessage());
                    Toaster.showLong(EspacepronoFragmentActivity.this, R.string.error_loading_list_grille);
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

                Toaster.showLong(EspacepronoFragmentActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {}

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                grilleTask = null;
            }
        };
        grilleTask.execute();
    }

}