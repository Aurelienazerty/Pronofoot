package com.pronofoot.teamazerty.app.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.util.Ln;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_LOUGOUT;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected Context context;
    protected AccountManager accountManager;

    @Inject
    public LogoutService(Context context, AccountManager accountManager) {
        this.context = context;
        this.accountManager = accountManager;
    }

    public void logout(final Runnable onSuccess) {

        new LogoutTask(context, onSuccess).execute();
    }

    private static class LogoutTask extends SafeAsyncTask<Boolean> {

        private final Context context;
        private Runnable onSuccess;
        private String regId;

        protected LogoutTask(Context context, Runnable onSuccess) {
            this.context = context;
            this.onSuccess = onSuccess;
            //Pour le push
            try {
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
        }

        @Override
        public Boolean call() throws Exception {
            final Account[] accounts = AccountManager.get(context).getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
            if(accounts.length > 0) {
                AccountManagerFuture<Boolean> removeAccountFuture = AccountManager.get(context).removeAccount
                        (accounts[0], null, null);


                SharedPreferences preferences = context.getSharedPreferences(Constants.Pref.PREF_NAME, 0);

                //Envoyer post pour supprimer les notifs
                String username = preferences.getString(Constants.Pref.PREF_LOGIN, "-1");
                String password = preferences.getString(Constants.Pref.PREF_PASSWORD, "-1");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL_LOUGOUT);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                final String user_lang = Locale.getDefault().getLanguage();
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_GCM_REGID, regId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Supprimer les prefs
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.Pref.PREF_LOGIN, "");
                editor.putString(Constants.Pref.PREF_PASSWORD, "");
                editor.putString(Constants.Pref.PREF_USER_ID, "");
                editor.commit();

                if(removeAccountFuture.getResult() == true) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        @Override
        protected void onSuccess(Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);
            Ln.d("Logout succeeded: %s", accountWasRemoved);
            onSuccess.run();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Ln.e(e.getCause(), "Logout failed.");
        }
    }
}
