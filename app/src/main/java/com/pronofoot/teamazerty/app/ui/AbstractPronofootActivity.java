package com.pronofoot.teamazerty.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.core.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Views;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Base activity for a Bootstrap activity which does not use fragments.
 */
public abstract class AbstractPronofootActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        // Used to inject views with the Butterknife library
        Views.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // This is the home button in the top left corner of the screen.
                // Dont call finish! Because activity could have been started by an outside activity and the home button would not operated as expected!
                Intent homeIntent = new Intent(this, CarouselActivity.class);
                homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                homeIntent.putExtra(Constants.Indent.LUNCHED, true);
                startActivity(homeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @see PronofootFragmentActivity
     * @return List<NameValuePair>
     */
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
