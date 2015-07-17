package com.pronofoot.teamazerty.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.google.android.gcm.GCMRegistrar;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;

/**
 * Created by PBVZ9205 on 23/05/2014.
 */
public class ProfilActivity extends AbstractPronofootActivity {
    @InjectView(R.id.switch_notif)
    Switch notif;
    @InjectView(R.id.switch_info)
    Switch info;
    @InjectView(R.id.switch_newsletter)
    Switch newsletter;
    @InjectView(R.id.switch_home)
    Switch homePage;
    @InjectView(R.id.switch_publicite)
    Switch publicite;
    @InjectView(R.id.profil_username)
    TextView lbl_username;
    @InjectView(R.id.profil_loading)
    ProgressBar progressBar;
    @InjectView(R.id.b_save_profil)
    Button buttonSave;
    @InjectView(R.id.b_more_profil)
    Button buttonMore;
    @InjectView(R.id.version)
    TextView tv_version;

    private SafeAsyncTask<Boolean> profilTask;

    private boolean val_notif;
    private boolean val_info;
    private boolean val_newsletter;
    private boolean val_homePage;
    private boolean val_publicite;
    private String val_username;
    private String regId;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);

        Context context = getApplicationContext();
        try {
            version = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "1.0.0";
        }
        tv_version.setText(context.getString(R.string.app_name) + " " + version);

        chargerProfile(false);

    }

    /**
     * Charge le profil de l'utilisateur
     * Si want notif est spécifié, alors on modifie le profile
     * @param edit
     */
    private void chargerProfile(final boolean edit) {
        //Pour le push
        try {
            Context context = getApplicationContext();
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
        showProgress();
        profilTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Boolean res = false;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                try {
                    SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);

                    final String user_id = preferences.getString(Constants.Pref.PREF_USER_ID, "-1");
                    final String username = preferences.getString(Constants.Pref.PREF_LOGIN, "-1");
                    final String password = preferences.getString(Constants.Pref.PREF_PASSWORD, "-1");
                    final String user_lang = Locale.getDefault().getLanguage();

                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USER_ID, user_id));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERMAIL, username));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_ANDROID, Build.VERSION.RELEASE));
                    if (edit) {
                        String state;
                        if (notif.isChecked()) {
                            state = "y";
                        } else {
                            state = "n";
                        }
                        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_NOTIF, state));
                        if (info.isChecked()) {
                            state = "y";
                        } else {
                            state = "n";
                        }
                        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_NEWSLETTER, state));
                        if (newsletter.isChecked()) {
                            state = "y";
                        } else {
                            state = "n";
                        }
                        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_NEWSLETTER, state));


                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Constants.Param.PARAM_HOME_PAGE_IS_RESULT, homePage.isChecked());
                        editor.putBoolean(Constants.Param.PARAM_WANT_PUB, publicite.isChecked());
                        editor.apply();
                    }

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.Http.URL_PROFIL);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String dataRes = EntityUtils.toString(entity);
                    JSONObject jsonResponse = new JSONObject(dataRes);
                    val_username = jsonResponse.getString(Constants.Param.PARAM_USERNAME);
                    val_notif = jsonResponse.getString(Constants.Param.PARAM_NOTIF).equalsIgnoreCase("y");
                    val_info = jsonResponse.getString(Constants.Param.PARAM_INFO).equalsIgnoreCase("y");
                    val_newsletter = jsonResponse.getString(Constants.Param.PARAM_NEWSLETTER).equalsIgnoreCase("y");

                    val_homePage = preferences.getBoolean(Constants.Param.PARAM_HOME_PAGE_IS_RESULT, false);
                    val_publicite = preferences.getBoolean(Constants.Param.PARAM_WANT_PUB, true);

                    res = true;
                } catch (IOException e) {
                    if (edit) {
                        Toaster.showLong(ProfilActivity.this, R.string.error_loading_profil);
                    } else {
                        Toaster.showLong(ProfilActivity.this, R.string.error_modifier_profil);
                    }
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

                Toaster.showLong(ProfilActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {
                hideProgress();
                lbl_username.setText(val_username);
                notif.setChecked(val_notif);
                info.setChecked(val_info);
                newsletter.setChecked(val_newsletter);
                homePage.setChecked(val_homePage);
                publicite.setChecked(val_publicite);
                profilTask = null;
            }

            @Override
            protected void onFinally() throws RuntimeException {
                //hideProgress();
            }
        };
        profilTask.execute();
    }

    public void saveProfil(View button) {
        chargerProfile(true);
    }

    public void moreProfil(View button) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Http.PROFIL));
        startActivity(browserIntent);
    }

    /**
     * Hide progress dialog
     */
    protected void hideProgress() {
        lbl_username.setVisibility(View.VISIBLE);
        notif.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        newsletter.setVisibility(View.VISIBLE);
        homePage.setVisibility(View.VISIBLE);
        publicite.setVisibility(View.INVISIBLE);//Option cachée pour le moment
        buttonSave.setVisibility(View.VISIBLE);
        buttonMore.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected void showProgress() {
        lbl_username.setVisibility(View.INVISIBLE);
        notif.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);
        newsletter.setVisibility(View.INVISIBLE);
        homePage.setVisibility(View.INVISIBLE);
        publicite.setVisibility(View.INVISIBLE);
        buttonSave.setVisibility(View.INVISIBLE);
        buttonMore.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
