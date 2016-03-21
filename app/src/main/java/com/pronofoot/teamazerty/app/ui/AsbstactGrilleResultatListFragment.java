package com.pronofoot.teamazerty.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;
import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.authenticator.LogoutService;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.Grille;

import javax.inject.Inject;

/**
 * Created by PBVZ9205 on 23/05/2014.
 */
abstract class AsbstactGrilleResultatListFragment<Match> extends ItemListFragment<Match> {

    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    protected LogoutService logoutService;

    protected View header, footer;

    protected Grille response;
    protected String nomGrille;
    protected int id_grille;
    protected boolean matchNull = false;
    protected boolean prolongations = false;

    //Propriétes partagées
    protected String user_id, username, password;
    protected String regId;
    protected String version = "1.0.0";

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.Pref.PREF_NAME, 0);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EspacepronoFragmentActivity) {
            ((EspacepronoFragmentActivity) activity).addFragment(this);
        } else {
            ((ResultatUserActivity) activity).addFragment(this);
        }
    }

    abstract void actualiserAffichage();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = EspacepronoResultatListFragment.getSharedPreferences(AsbstactGrilleResultatListFragment.this.getActivity().getApplicationContext());
        user_id = preferences.getString(Constants.Pref.PREF_USER_ID, "-1");
        username = preferences.getString(Constants.Pref.PREF_LOGIN, "-1");
        password = preferences.getString(Constants.Pref.PREF_PASSWORD, "-1");
        try {
            version = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Pour le push
        try {
            Context context = getActivity().getApplicationContext();
            GCMRegistrar.checkDevice(context);
            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(context);
            GCMRegistrar.register(context, Constants.Push.GOOGLE_PROJECT_ID);
            // Get GCM registration id
            regId = GCMRegistrar.getRegistrationId(context);
        } catch (Exception e) {
            regId = Constants.Extra.VIELLE_VERSION;
        }
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(Html.fromHtml(getString(R.string.no_grille)));

    }

    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
    }

    @Override
    public LogoutService getLogoutService() {
        return logoutService;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_grille;
    }
}
