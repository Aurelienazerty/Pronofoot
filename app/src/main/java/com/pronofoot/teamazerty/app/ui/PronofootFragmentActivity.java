package com.pronofoot.teamazerty.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.core.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Views;

/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class PronofootFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        Views.inject(this);
    }

    public List<NameValuePair> preparePost() {
        SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        final String user_id = preferences.getString(Constants.Pref.PREF_USER_ID, "-1");
        final String username = preferences.getString(Constants.Pref.PREF_LOGIN, "-1");
        final String password = preferences.getString(Constants.Pref.PREF_PASSWORD, "-1");
        final String user_lang = Locale.getDefault().getLanguage();
        String versionName;
        try {
            versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "NA";
        }

        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USER_ID, user_id));
        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERMAIL, username));
        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));
        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
        nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_ANDROID, versionName));

        return nameValuePairs;
    }

}
