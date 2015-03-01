
package com.pronofoot.teamazerty.app.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.github.kevinsawicki.wishlist.Toaster;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.core.User;
import com.pronofoot.teamazerty.app.ui.RegisterActivity;
import com.pronofoot.teamazerty.app.ui.TextWatcherAdapter;
import com.pronofoot.teamazerty.app.util.Ln;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.Views;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_AUTH;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class BootstrapAuthenticatorActivity extends SherlockAccountAuthenticatorActivity {

    private AccountManager accountManager;

    @InjectView(R.id.et_username)
    EditText usernameText;
    @InjectView(R.id.et_password)
    EditText passwordText;
    @InjectView(R.id.b_signin)
    Button signinButton;
    @InjectView(R.id.b_signup)
    Button signupButton;
    @InjectView(R.id.login_loading)
    ProgressBar progressBar;
    @InjectView(R.id.et_lblusername)
    TextView lblUsername;
    @InjectView(R.id.et_lblpassword)
    TextView lblPassword;
    @InjectView(R.id.lost_password)
    TextView lostpassword;

    private TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

    private String username;
    private String password;
    public User authUser = null;
    private String regId;
    private String version;


    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
        String user_id = preferences.getString(Constants.Pref.PREF_USER_ID, "");
        String username = preferences.getString(Constants.Pref.PREF_LOGIN, "");
        String password = preferences.getString(Constants.Pref.PREF_PASSWORD, "");
        if (!username.isEmpty() && !user_id.isEmpty() && !password.isEmpty()) {
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        super.onCreate(bundle);

        accountManager = AccountManager.get(this);
        final Intent intent = getIntent();
        username = intent.getStringExtra(Constants.Param.PARAM_PASSWORD);
        authTokenType = intent.getStringExtra(Constants.Param.PARAM_AUTHTOKEN_TYPE);
        requestNewAccount = username == null;
        confirmCredentials = intent.getBooleanExtra(Constants.Param.PARAM_CONFIRMCREDENTIALS,
                false);

        setContentView(R.layout.login_activity);
        Views.inject(this);

        /*usernameText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));*/

        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signinButton.isEnabled()) {
                    handleLogin(signinButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signinButton.isEnabled()) {
                    handleLogin(signinButton);
                    return true;
                }
                return false;
            }
        });

        usernameText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        TextView signupText = (TextView) findViewById(R.id.tv_signup);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
        signupText.setText(Html.fromHtml(getString(R.string.signup_link)));
        lostpassword.setMovementMethod(LinkMovementMethod.getInstance());
        lostpassword.setText(Html.fromHtml(getString(R.string.lost_password)));

        progressBar.setVisibility(View.INVISIBLE);
    }

    /*private List<String> userEmailAccounts() {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (Account account : accounts)
            emailAddresses.add(account.name);
        return emailAddresses;
    }*/

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        boolean populated = populated(usernameText) && populated(passwordText);
        signinButton.setEnabled(populated);
    }

    private boolean populated(EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (authenticationTask != null)
                    authenticationTask.cancel(true);
            }
        });
        return dialog;
    }

    public void goRegister(View view) {
        Intent profile = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(profile);
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(View view) {
        if (authenticationTask != null) {
            return;
        }

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        //Pour le push
        Context context = getApplicationContext();
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

        try {
            version = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                Boolean res = false;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL_AUTH);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                final String user_lang = Locale.getDefault().getLanguage();
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_GCM_REGID, regId));
                nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_VERSION, version));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String dataRes = EntityUtils.toString(entity);

                //Log.i("TA: retour", dataRes);

                JSONObject jsonResponse = new JSONObject(dataRes);
                SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                if (jsonResponse.getString("etat").equalsIgnoreCase("ok")) {
                    res = true;
                    String userString = jsonResponse.getString("object");
                    authUser = new Gson().fromJson(userString, User.class);
                    //Context context = new inContext();
                    editor.putString(Constants.Pref.PREF_LOGIN, authUser.getUsername());
                    editor.putString(Constants.Pref.PREF_PASSWORD, authUser.getUser_password());
                    editor.putString(Constants.Pref.PREF_USER_ID, authUser.getUser_id());
                    editor.commit();
                } else {
                    editor.putString(Constants.Pref.PREF_LOGIN, "");
                    editor.putString(Constants.Pref.PREF_PASSWORD, "");
                    editor.putString(Constants.Pref.PREF_USER_ID, "");
                    editor.commit();
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

                Toaster.showLong(BootstrapAuthenticatorActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(boolean result) {
        final Account account = new Account(username, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.setPassword(account, password);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(username, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (requestNewAccount) {
            accountManager.addAccountExplicitly(account, password, null);
        } else {
            accountManager.setPassword(account, password);
        }
        final Intent intent = new Intent();
        authToken = token;
        intent.putExtra(KEY_ACCOUNT_NAME, username);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        lblUsername.setVisibility(View.VISIBLE);
        lblPassword.setVisibility(View.VISIBLE);
        usernameText.setVisibility(View.VISIBLE);
        passwordText.setVisibility(View.VISIBLE);
        signinButton.setVisibility(View.VISIBLE);
        signupButton.setVisibility(View.VISIBLE);
        lostpassword.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        lblUsername.setVisibility(View.INVISIBLE);
        lblPassword.setVisibility(View.INVISIBLE);
        usernameText.setVisibility(View.INVISIBLE);
        passwordText.setVisibility(View.INVISIBLE);
        signinButton.setVisibility(View.INVISIBLE);
        signupButton.setVisibility(View.INVISIBLE);
        lostpassword.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(boolean result) {
        if (result) {
            if (!confirmCredentials) {
                finishLogin();
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            if (requestNewAccount)
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        R.string.message_auth_failed_new_account);
            else
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        R.string.message_auth_failed);
        }
    }
}
